package org.example.orderservice.service;

import org.example.orderservice.dto.OrderItemResponseDTO;
import org.example.orderservice.dto.OrderItemResquestDTO;

import java.util.List;

public interface OrderItemService {
    OrderItemResponseDTO createOrderItem(OrderItemResquestDTO request);
    List<OrderItemResponseDTO> getOrderItems(Long orderId);
    OrderItemResponseDTO updateOrderItem(Long itemId, int quantity);
    void deleteOrderItem(Long itemId);

}
