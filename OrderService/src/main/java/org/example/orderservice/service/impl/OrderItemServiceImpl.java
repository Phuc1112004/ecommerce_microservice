package org.example.orderservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.example.common.client.BookClient;
import org.example.common.dto.BookInfoDTO;
import org.example.common.exception.ResourceNotFoundException;
import org.example.orderservice.dto.OrderItemResponseDTO;
import org.example.orderservice.dto.OrderItemResquestDTO;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.Orders;
import org.example.orderservice.repository.OrderItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    // ----------------- Thêm OrderItem -----------------
    @Transactional
    public OrderItemResponseDTO createOrderItem(OrderItemResquestDTO request) {
        Orders order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order không tồn tại"));

        BookInfoDTO book = bookClient.getBookById(request.getBookId());

        if (book == null) {
            throw new ResourceNotFoundException("Book không tồn tại");
        }

        if (book.getStockQuantity() < request.getQuantity()) {
            throw new RuntimeException("Không đủ số lượng tồn kho cho sách: " + book.getTitle());
        }

        // Trừ tồn kho bên catalog-service
        // quantity truyền số âm để giảm stock
        bookClient.updateStockQuantity(request.getBookId(), -request.getQuantity());

        OrderItem item = new OrderItem();
        item.setOrderId(order.getOrderId());
        item.setBookId(book.getBookId());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice() != null ? request.getPrice() : book.getSalePrice());

        OrderItem saved = orderItemRepository.save(item);

        // Map kết quả
        OrderItemResponseDTO response = mapToDTO(saved);
        response.setBookTitle(book.getTitle());
        return response;
    }

    // ----------------- Lấy tất cả item theo Order -----------------
    public List<OrderItemResponseDTO> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .stream()
                .map(item -> {
                    OrderItemResponseDTO dto = mapToDTO(item);
                    // gọi bookClient để lấy thông tin title
                    BookInfoDTO book = bookClient.getBookById(item.getBookId());
                    if (book != null) {
                        dto.setBookTitle(book.getTitle());
                    }
                    return dto;
                })
                .toList();
    }

    // ----------------- Cập nhật số lượng -----------------
    @Transactional
    public OrderItemResponseDTO updateOrderItem(Long itemId, int newQuantity) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem không tồn tại"));


        int diff = newQuantity - item.getQuantity();

        if (diff == 0) return mapToDTO(item);

        // Gọi sang catalog để kiểm tra tồn kho
        BookInfoDTO book = bookClient.getBookById(item.getBookId());
        if (book == null) throw new ResourceNotFoundException("Book không tồn tại");

        if (diff > 0 && book.getStockQuantity() < diff) {
            throw new RuntimeException("Không đủ tồn kho để cập nhật số lượng");
        }

        // Cập nhật tồn kho (âm = trừ, dương = cộng)
        bookClient.updateStockQuantity(item.getBookId(), -diff);
        item.setQuantity(newQuantity);
        OrderItem saved = orderItemRepository.save(item);

        OrderItemResponseDTO dto = mapToDTO(saved);
        dto.setBookTitle(book.getTitle());
        return dto;
    }

    // ----------------- Xóa OrderItem -----------------
    @Transactional
    public void deleteOrderItem(Long itemId) {
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem không tồn tại"));

        // trả lại stock
        bookClient.updateStockQuantity(item.getBookId(), item.getQuantity());

        orderItemRepository.delete(item);
    }

    // ----------------- Map entity → DTO -----------------
    private OrderItemResponseDTO mapToDTO(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setOrderItemId(item.getOrderItemId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setOrderId(item.getOrderId());
        dto.setBookId(item.getBookId());
        return dto;
    }
}
