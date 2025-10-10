package org.example.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "purchase_item")
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseItemId;
    private int quantity;
    private Long unitPrice;

    private Long purchaseId; // chỉ lưu ID, không tham chiếu entity khác
    private Long bookId;     // chỉ lưu ID

//    @ManyToOne
//    @JoinColumn(name = "purchase_id")
//    private Purchase purchase;
//
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Books books;

}
