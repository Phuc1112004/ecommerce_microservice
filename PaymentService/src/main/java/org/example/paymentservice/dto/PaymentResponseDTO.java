package org.example.paymentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private Long paymentId;
    private Long orderId;
    private String paymentMethod;
    private Long amount;
    private String status;
    private LocalDateTime paidAt;
}