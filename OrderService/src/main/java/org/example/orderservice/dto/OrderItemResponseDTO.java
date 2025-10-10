package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDTO {
    private Long orderItemId;
    private Long orderId;
    private Long bookId;
    private String bookTitle;   // để client hiển thị
    private Long price;         // giá bán lúc tạo order
    private Integer quantity;
}
