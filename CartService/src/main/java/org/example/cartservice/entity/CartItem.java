package org.example.cartservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartItemId;

    private Long cartId;
    private Long bookId;

//    @ManyToOne
//    @JoinColumn(name = "cart_id")
//    private Cart cart;
//
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Books books;

    private Integer quantity;
}
