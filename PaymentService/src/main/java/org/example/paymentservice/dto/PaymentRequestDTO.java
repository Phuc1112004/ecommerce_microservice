package org.example.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long orderId;
    private String paymentMethod;
    private Long amount;
}
