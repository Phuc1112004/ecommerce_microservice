package org.example.orderservice.dto;


import lombok.Data;
import org.example.orderservice.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long orderId;
    private Long userId;
    private String receiver;
    private Long totalAmount;
    private OrderStatus status;               // pending, paid, shipped...
    private String shippingAddress;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> listItems; // chi tiết từng sách

}
