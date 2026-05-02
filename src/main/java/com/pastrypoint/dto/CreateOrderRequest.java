package com.pastrypoint.dto;

import com.pastrypoint.model.PaymentProvider;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String customerName;
    private String customerPhone;
    private PaymentProvider paymentProvider;
    private List<OrderItemRequest> items;

    // Delivery fields
    private String orderType;       // DINE_IN or DELIVERY
    private String deliveryAddress;
    private String scheduleTime;    // now, 30min, 1hr, 2hr, custom
}
