package org.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDTO {
    private Long paymentId;
    private Long orderId;
    private Double amount;
    private String status; // PENDING, SUCCESS, FAILED
    private String method; // VNPay, Momo, etc.
}

