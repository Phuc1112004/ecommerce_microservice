package org.example.orderservice.controller;

import jakarta.validation.Valid;
import org.example.common.dto.OrderInfoDTO;
import org.example.common.dto.kafka.OrderItemDTO;
import org.example.orderservice.dto.OrderRequestDTO;
import org.example.orderservice.dto.OrderResponseDTO;
import org.example.orderservice.entity.Orders;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repository.OrderItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Tạo đơn hàng
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public OrderResponseDTO createOrders(@RequestBody @Valid OrderRequestDTO request) {
        return orderService.createOrder(request);
    }

    // Lấy tất cả đơn hàng
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    // Lấy chi tiết đơn hàng theo ID
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public OrderResponseDTO getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // Cập nhật trạng thái đơn hàng
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO updateOrderStatusForAdmin(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        return orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrderResponseDTO>> searchOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo
    ) {
        return ResponseEntity.ok(orderService.searchOrders(keyword, status, dateFrom, dateTo));
    }

    @GetMapping("/{orderId}/info")
    public OrderInfoDTO getOrderInfo(@PathVariable Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderInfoDTO dto = new OrderInfoDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setReceiver(order.getReceiver());
        dto.setShippingAddress(order.getShippingAddress());
        return dto;
    }

    // --- API nội bộ, được PaymentService gọi qua Feign ---
    @PutMapping("/{orderId}/internal-status")
    public ResponseEntity<?> updateOrderStatusInternal(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);

        return ResponseEntity.ok("Order status updated to " + status);
    }


    @GetMapping("/{orderId}/internal-info")
    public OrderInfoDTO getInternalOrderInfo(@PathVariable Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Lấy danh sách item
        List<OrderItemDTO> items = orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(item -> new OrderItemDTO(item.getBookId(), item.getQuantity()))
                .toList();

        OrderInfoDTO dto = new OrderInfoDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setReceiver(order.getReceiver());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setItems(items); // <--- thêm dòng này

        return dto;
    }


}
