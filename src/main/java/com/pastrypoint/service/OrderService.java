package com.pastrypoint.service;

import com.pastrypoint.dto.CreateOrderRequest;
import com.pastrypoint.dto.OrderItemRequest;
import com.pastrypoint.model.*;
import com.pastrypoint.repository.OrderRepository;
import com.pastrypoint.repository.ProductRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, LocalDateTime> rateLimit = new ConcurrentHashMap<>();

    @Value("${razorpay.key.id:}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret:}")
    private String razorpayKeySecret;

    @Transactional
    public CustomerOrder createOrder(CreateOrderRequest request, String idempotencyKey, String deviceId, String clientIp) throws Exception {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Idempotency-Key");
        }

        var existingOrder = orderRepository.findByIdempotencyKey(idempotencyKey);
        if (existingOrder.isPresent()) {
            return existingOrder.get();
        }

        enforceRateLimit(deviceId, clientIp);
        validateRequest(request);

        PaymentProvider provider = request.getPaymentProvider() == null ? PaymentProvider.CASH : request.getPaymentProvider();
        if (provider == PaymentProvider.CASH) {
            rejectActiveUnpaidCashOrder(deviceId, clientIp);
        }

        CustomerOrder order = new CustomerOrder();
        order.setIdempotencyKey(idempotencyKey);
        order.setDeviceId(clean(deviceId));
        order.setClientIp(clean(clientIp));
        order.setCustomerName(request.getCustomerName().trim());
        order.setCustomerPhone(clean(request.getCustomerPhone()));
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setTokenNumber("#" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setPaymentProvider(provider);

        // Delivery fields
        order.setOrderType(request.getOrderType() != null ? request.getOrderType() : "DINE_IN");
        order.setDeliveryAddress(clean(request.getDeliveryAddress()));
        order.setScheduleTime(request.getScheduleTime() != null ? request.getScheduleTime() : "now");

        populateItemsAndTotal(order, request.getItems());

        if (provider == PaymentProvider.CASH) {
            order.setPaymentStatus(PaymentStatus.PENDING_CASH);
            order.setOrderStatus(OrderStatus.PLACED);
        } else if (provider == PaymentProvider.RAZORPAY) {
            createRazorpayOrder(order);
            order.setPaymentStatus(PaymentStatus.PENDING_ONLINE);
            order.setOrderStatus(OrderStatus.DRAFT);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PhonePe sandbox is planned but not enabled in this build");
        }

        try {
            CustomerOrder savedOrder = orderRepository.save(order);
            messagingTemplate.convertAndSend("/topic/orders", savedOrder);
            return savedOrder;
        } catch (DataIntegrityViolationException e) {
            return orderRepository.findByIdempotencyKey(idempotencyKey).orElseThrow();
        }
    }

    @Transactional
    public CustomerOrder verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws Exception {
        if (razorpayOrderId == null || razorpayPaymentId == null || razorpaySignature == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing Razorpay verification fields");
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", razorpayOrderId);
        options.put("razorpay_payment_id", razorpayPaymentId);
        options.put("razorpay_signature", razorpaySignature);

        if (!Utils.verifyPaymentSignature(options, razorpayKeySecret)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment signature verification failed");
        }

        CustomerOrder order = orderRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        order.setPaymentStatus(PaymentStatus.PAID_ONLINE);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setRazorpayPaymentId(razorpayPaymentId);
        order.setUpdatedAt(LocalDateTime.now());

        CustomerOrder savedOrder = orderRepository.save(order);
        messagingTemplate.convertAndSend("/topic/orders", savedOrder);
        messagingTemplate.convertAndSend("/topic/orders/refresh", "REFRESH_NEEDED");
        return savedOrder;
    }

    @Transactional
    public CustomerOrder updateStatus(Long id, OrderStatus status) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getOrderStatus() == OrderStatus.DRAFT && status != OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pending online payment cannot be packed");
        }

        order.setOrderStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        if ((status == OrderStatus.PACKING || status == OrderStatus.HANDED_OVER)
                && order.getPaymentStatus() == PaymentStatus.PENDING_CASH) {
            order.setPaymentStatus(PaymentStatus.PAID_CASH);
        }

        if (status == OrderStatus.CANCELLED && order.getPaymentStatus() != PaymentStatus.FAILED) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            releaseInventory(order);
        }

        CustomerOrder savedOrder = orderRepository.save(order);
        messagingTemplate.convertAndSend("/topic/orders", savedOrder);
        messagingTemplate.convertAndSend("/topic/orders/refresh", "REFRESH_NEEDED");
        return savedOrder;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredPendingOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(10);
        List<CustomerOrder> expiredOrders = new ArrayList<>();
        expiredOrders.addAll(orderRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING_CASH, cutoffTime));
        expiredOrders.addAll(orderRepository.findByPaymentStatusAndCreatedAtBefore(PaymentStatus.PENDING_ONLINE, cutoffTime));

        for (CustomerOrder order : expiredOrders) {
            if (order.getOrderStatus() != OrderStatus.CANCELLED && order.getPaymentStatus() != PaymentStatus.FAILED) {
                order.setOrderStatus(OrderStatus.CANCELLED);
                order.setPaymentStatus(PaymentStatus.FAILED);
                order.setUpdatedAt(LocalDateTime.now());
                releaseInventory(order);
                orderRepository.save(order);
            }
        }

        if (!expiredOrders.isEmpty()) {
            messagingTemplate.convertAndSend("/topic/orders/refresh", "REFRESH_NEEDED");
        }
    }

    private void validateRequest(CreateOrderRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order request is required");
        }
        if (request.getCustomerName() == null || request.getCustomerName().trim().length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer name is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }
    }

    private void populateItemsAndTotal(CustomerOrder order, List<OrderItemRequest> requests) {
        Map<Long, Integer> quantities = requests.stream()
                .filter(item -> item.getProductId() != null && item.getQuantity() != null && item.getQuantity() > 0)
                .collect(Collectors.groupingBy(OrderItemRequest::getProductId, Collectors.summingInt(OrderItemRequest::getQuantity)));

        if (quantities.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        double total = 0;
        List<String> summary = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = productRepository.findByIdWithLock(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            int quantity = entry.getValue();

            if (!product.isInStock()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, product.getName() + " is sold out");
            }
            if (product.getStockQuantity() != null) {
                if (product.getStockQuantity() < quantity) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, product.getName() + " has only " + product.getStockQuantity() + " left");
                }
                product.setStockQuantity(product.getStockQuantity() - quantity);
                if (product.getStockQuantity() == 0) {
                    product.setInStock(false);
                }
                productRepository.save(product);
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setCategory(product.getCategory());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setLineTotal(product.getPrice() * quantity);
            order.getItems().add(item);

            total += item.getLineTotal();
            summary.add(quantity + "x " + product.getName());
        }

        order.setTotalAmount(total);
        order.setItemsSummary(String.join(" | ", summary));
    }

    private void createRazorpayOrder(CustomerOrder order) throws Exception {
        if (razorpayKeyId == null || razorpayKeyId.isBlank() || razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Razorpay keys are not configured");
        }

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", Math.round(order.getTotalAmount() * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", order.getTokenNumber());

        Order razorpayOrder = razorpayClient.orders.create(orderRequest);
        order.setRazorpayOrderId(razorpayOrder.get("id"));
    }

    private void rejectActiveUnpaidCashOrder(String deviceId, String clientIp) {
        if (deviceId != null && !deviceId.isBlank()) {
            orderRepository.findFirstByDeviceIdAndPaymentStatusAndOrderStatusNot(deviceId, PaymentStatus.PENDING_CASH, OrderStatus.CANCELLED)
                    .ifPresent(order -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Pay or cancel your existing cash token first");
                    });
        }
        if (clientIp != null && !clientIp.isBlank()) {
            orderRepository.findFirstByClientIpAndPaymentStatusAndOrderStatusNot(clientIp, PaymentStatus.PENDING_CASH, OrderStatus.CANCELLED)
                    .ifPresent(order -> {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Pay or cancel your existing cash token first");
                    });
        }
    }

    private void enforceRateLimit(String deviceId, String clientIp) {
        String key = (deviceId != null && !deviceId.isBlank()) ? "device:" + deviceId : "ip:" + clientIp;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last = rateLimit.get(key);
        if (last != null && last.isAfter(now.minusSeconds(5))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Please wait before placing another order");
        }
        rateLimit.put(key, now);
    }

    private void releaseInventory(CustomerOrder order) {
        for (OrderItem item : order.getItems()) {
            productRepository.findByIdWithLock(item.getProductId()).ifPresent(product -> {
                if (product.getStockQuantity() != null) {
                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                    product.setInStock(true);
                    productRepository.save(product);
                }
            });
        }
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
