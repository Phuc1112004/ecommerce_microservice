package org.example.common.dto.kafka;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSuccessEvent {
    private Long orderId;
    private Long userId;
    private Long amount;
    private List<OrderItemDTO> items;
}