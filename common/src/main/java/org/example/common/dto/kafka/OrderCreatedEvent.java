package org.example.common.dto.kafka;

import lombok.*;
import org.example.common.enums.OrderStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private Long userId;
    private Long totalAmount;
    private String receiver;
    private String shippingAddress;
    private OrderStatus status;  // ví dụ: "PENDING"
    private LocalDateTime createdAt;
}