package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private Integer quantity;

    private Long price;

    private Long orderId;
    private Long bookId;

//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Orders orders; // null -> giỏ hàng, != null -> đã order
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Books books;
}
