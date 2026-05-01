package com.pastrypoint.repository;

import com.pastrypoint.model.CustomerOrder;
import com.pastrypoint.model.OrderStatus;
import com.pastrypoint.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime time);
    List<CustomerOrder> findAllByOrderByCreatedAtDesc();
    Optional<CustomerOrder> findByIdempotencyKey(String idempotencyKey);
    List<CustomerOrder> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    Optional<CustomerOrder> findFirstByDeviceIdAndPaymentStatusAndOrderStatusNot(String deviceId, PaymentStatus status, OrderStatus orderStatus);
    Optional<CustomerOrder> findFirstByClientIpAndPaymentStatusAndOrderStatusNot(String clientIp, PaymentStatus status, OrderStatus orderStatus);
    Optional<CustomerOrder> findByRazorpayOrderId(String razorpayOrderId);
}
