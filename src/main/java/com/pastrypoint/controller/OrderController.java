package com.pastrypoint.controller;

import com.pastrypoint.dto.CreateOrderRequest;
import com.pastrypoint.model.CustomerOrder;
import com.pastrypoint.model.OrderStatus;
import com.pastrypoint.repository.OrderRepository;
import com.pastrypoint.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/create")
    public ResponseEntity<CustomerOrder> placeOrder(
            @RequestBody CreateOrderRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceId,
            HttpServletRequest servletRequest) throws Exception {
        return ResponseEntity.ok(orderService.createOrder(request, idempotencyKey, deviceId, clientIp(servletRequest)));
    }

    @PostMapping("/verify")
    public ResponseEntity<CustomerOrder> verifyPayment(@RequestBody Map<String, String> data) throws Exception {
        return ResponseEntity.ok(orderService.verifyPayment(
                data.get("razorpay_order_id"),
                data.get("razorpay_payment_id"),
                data.get("razorpay_signature")
        ));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CustomerOrder>> getActiveOrders() {
        return ResponseEntity.ok(orderRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/history")
    public ResponseEntity<List<CustomerOrder>> getOrderHistory(@RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        LocalDateTime start = parsedDate.atStartOfDay();
        LocalDateTime end = parsedDate.plusDays(1).atStartOfDay().minusNanos(1);
        return ResponseEntity.ok(orderRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<CustomerOrder> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @PostMapping("/{id}/pay-cash")
    public ResponseEntity<CustomerOrder> markAsPaid(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.markAsPaid(id));
    }

    @DeleteMapping("/clear-test")
    public ResponseEntity<Void> clearTestOrders() {
        orderRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
