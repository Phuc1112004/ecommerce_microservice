package org.example.cartservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Data
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private Users user;
//
//    @OneToMany(mappedBy = "cart")
//    private List<CartItem> listCartItems;

}
