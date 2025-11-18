package org.example.common.dto.kafka;

import lombok.*;
import org.example.common.enums.OrderStatus;
import org.example.common.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private Long totalAmount;
    private String receiver;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private OrderStatus status;  // ví dụ: "PENDING"
    private LocalDateTime createdAt;

    private List<OrderItemDTO> items;
}