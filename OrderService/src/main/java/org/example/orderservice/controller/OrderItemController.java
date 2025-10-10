package org.example.orderservice.controller;


import org.example.orderservice.dto.OrderItemResponseDTO;
import org.example.orderservice.dto.OrderItemResquestDTO;
import org.example.orderservice.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {
    // đã tạo đơn hàng thì ko  được thay  đổi nếu mua thêm sẽ tạo đơn hàng khác

    @Autowired
    private OrderItemService orderItemService;

    // Tạo mới OrderItem
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<OrderItemResponseDTO> createOrderItems(@RequestBody OrderItemResquestDTO request) {
        OrderItemResponseDTO created = orderItemService.createOrderItem(request);
        return ResponseEntity.ok(created);
    }

    // Lấy danh sách OrderItem theo orderId
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<OrderItemResponseDTO>> getItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemResponseDTO> items = orderItemService.getOrderItems(orderId);
        return ResponseEntity.ok(items);
    }

//     Xóa OrderItem theo orderItemId
    @DeleteMapping("/{orderItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

//     Cập nhật số lượng OrderItem
    @PutMapping("/{orderItemId}/quantity/{quantity}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<OrderItemResponseDTO> updateQuantity(
            @PathVariable Long orderItemId,
            @PathVariable int quantity) {
        OrderItemResponseDTO updated = orderItemService.updateOrderItem(orderItemId, quantity);
        return ResponseEntity.ok(updated);
    }
}