package org.example.orderservice.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long totalPrice;
    private String status;   // e.g., "CREATED", "PAID", "CANCELLED"
}
