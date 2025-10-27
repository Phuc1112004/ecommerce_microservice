package org.example.orderservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.example.common.client.BookClient;
import org.example.common.client.UserClient;
import org.example.common.dto.BookInfoDTO;
import org.example.common.dto.UserInfoDTO;
import org.example.common.dto.kafka.OrderCreatedEvent;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.dto.OrderItemResponseDTO;
import org.example.orderservice.dto.OrderItemResquestDTO;
import org.example.orderservice.dto.OrderRequestDTO;
import org.example.orderservice.dto.OrderResponseDTO;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.Orders;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repository.OrderItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.security.SecurityUtils;
import org.example.orderservice.service.OrderService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;


    private final UserClient userClient;

    private final BookClient bookClient;

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    // Tạo đơn hàng mới
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1. Kiểm tra user
        Long userId = SecurityUtils.getCurrentUserId(); // lấy từ token

        if (userId == null) {
            throw new RuntimeException("User not authenticated");
        }

        // 2. Lưu order trước để lấy orderId
        Orders order = new Orders();
        order.setUserId(userId);
        order.setReceiver(request.getReceiver());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(0L);

        Orders savedOrder = orderRepository.save(order); // orderId được tạo

        long totalAmount = 0L;

        // 3. Lưu từng orderItem
        for (OrderItemResquestDTO itemReq : request.getListItems()) {
            BookInfoDTO book = bookClient.findBookById(itemReq.getBookId());
            if (book == null) throw new ResourceNotFoundException("Book not found");
            if (book.getStockQuantity() < itemReq.getQuantity()) throw new RuntimeException("Not enough stock");

            // trừ tồn kho bên catalog-service
            bookClient.updateStockQuantity(book.getBookId(), -itemReq.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getOrderId());
            item.setBookId(book.getBookId());
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(book.getSalePrice());

            orderItemRepository.save(item);
            totalAmount += book.getSalePrice() * itemReq.getQuantity();
        }

        // 4. Cập nhật tổng tiền
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);

        // 5. Gửi event sang Kafka (phải trước return)
        OrderCreatedEvent event = new OrderCreatedEvent();
        event.setOrderId(savedOrder.getOrderId());
        event.setUserId(savedOrder.getUserId());
        event.setTotalAmount(savedOrder.getTotalAmount());
        event.setReceiver(savedOrder.getReceiver());
        event.setShippingAddress(savedOrder.getShippingAddress());
        event.setStatus(org.example.common.enums.OrderStatus.PENDING);


        kafkaTemplate.send("order-created", event);
        System.out.println("✅ Sent OrderCreatedEvent to Kafka: " + event);

        OrderResponseDTO dto = convertToDTO(savedOrder);
        dto.setReceiver(request.getReceiver());

        return convertToDTO(savedOrder);
    }


    // Lấy đơn hàng theo ID
    public OrderResponseDTO getOrderById(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return convertToDTO(order);
    }

    // Lấy tất cả đơn hàng
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Orders findById(Long orderId) {
        Optional<Orders> order = orderRepository.findById(orderId);
        return order.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    public Orders save(Orders order){
        return orderRepository.save(order);
    }



    // Xóa đơn hàng
    public void deleteOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        orderRepository.delete(order);
    }

    // Cập nhật trạng thái đơn hàng
    public OrderResponseDTO updateOrderStatus(Long orderId, String status) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(order.getStatus());
        Orders updated = orderRepository.save(order);
        return convertToDTO(updated);
    }

    // Convert entity → DTO
    private OrderResponseDTO convertToDTO(Orders order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setReceiver(order.getReceiver());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus() : OrderStatus.PENDING);
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());

        // Lấy order items
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
        dto.setListItems(
                items.stream().map(item -> {
                    OrderItemResponseDTO itemDTO = new OrderItemResponseDTO();
                    itemDTO.setOrderItemId(item.getOrderItemId());
                    itemDTO.setBookId(item.getBookId());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());

                    BookInfoDTO book = bookClient.findBookById(item.getBookId());
                    if (book != null) itemDTO.setBookTitle(book.getTitle());

                    return itemDTO;
                }).toList()
        );

        return dto;
    }



    public List<OrderResponseDTO> searchOrders(String keyword,
                                               OrderStatus status,
                                               LocalDateTime dateFrom,
                                               LocalDateTime dateTo) {

        List<Orders> orders = orderRepository.searchOrders(status, dateFrom, dateTo);

        return orders.stream()
                .map(order -> {
                    UserInfoDTO user = userClient.getUserInfo(order.getUserId());
                    boolean match = keyword == null || keyword.isEmpty() ||
                            user.getUserName().toLowerCase().contains(keyword.toLowerCase()) ||
                            user.getPhone().contains(keyword); // vẫn search được

                    if (match) {
                        OrderResponseDTO dto = convertToDTO(order);
                        dto.setReceiver(user.getUserName()); // set userName thôi
                        return dto;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public void sendOrderEvent(OrderCreatedEvent event) {
        kafkaTemplate.send("order-topic", event);
    }
}
