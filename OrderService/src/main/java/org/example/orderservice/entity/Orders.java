package org.example.orderservice.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.example.orderservice.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String receiver;
    private Long totalAmount;
    private String shippingAddress;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Long userId;


//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private Users users;
//
//    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<OrderItem> listOrderItems;
//
//    @OneToMany(mappedBy = "orders")
//    private List<Payment> payment;
}
