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
}
