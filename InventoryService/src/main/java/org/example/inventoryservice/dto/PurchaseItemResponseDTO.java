package org.example.inventoryservice.dto;

import lombok.Data;

@Data
public class PurchaseItemResponseDTO {
    private Long purchaseItemId;
    private Long bookId;
    private String bookTitle;   // để hiển thị trong báo cáo
    private Integer quantity;
    private Long unitPrice;     // giá nhập từng sách
    private Long subtotal;  // quantity * unitPrice
}
