package com.pastrypoint.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "pastry_orders_v3")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String idempotencyKey;

    private String tokenNumber;
    private String customerName;
    private String customerPhone;
    private String deviceId;
    private String clientIp;
    private Double totalAmount;

    @Column(length = 2000)
    private String itemsSummary;

    private String razorpayOrderId;
    private String razorpayPaymentId;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;

    // Delivery fields
    private String orderType;       // DINE_IN or DELIVERY
    @Column(length = 500)
    private String deliveryAddress;
    private String scheduleTime;    // now, 30min, 1hr, 2hr, custom time

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
