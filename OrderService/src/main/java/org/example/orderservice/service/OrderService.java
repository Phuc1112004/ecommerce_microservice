package org.example.orderservice.service;

import org.example.orderservice.dto.OrderRequestDTO;
import org.example.orderservice.dto.OrderResponseDTO;
import org.example.orderservice.entity.Orders;
import org.example.orderservice.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    OrderResponseDTO getOrderById(Long orderId);
    List<OrderResponseDTO> getAllOrders();
    Orders findById(Long orderId);
    Orders save(Orders order);
    void deleteOrder(Long orderId);
    OrderResponseDTO updateOrderStatus(Long orderId, String status);
    List<OrderResponseDTO> searchOrders(String keyword,
                                        OrderStatus status,
                                        LocalDateTime dateFrom,
                                        LocalDateTime dateTo);

}
