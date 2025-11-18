package org.example.common.dto.kafka;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private Long orderId;
    private String reason;
}