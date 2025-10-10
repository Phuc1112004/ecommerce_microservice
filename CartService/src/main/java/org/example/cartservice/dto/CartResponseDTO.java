package org.example.cartservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartResponseDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemResponseDTO> listCartItems;
}
