package org.example.orderservice.controller;

import jakarta.validation.Valid;
import org.example.orderservice.dto.OrderRequestDTO;
import org.example.orderservice.dto.OrderResponseDTO;
import org.example.orderservice.enums.OrderStatus;
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
    public OrderResponseDTO updateOrderStatus(
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

}
