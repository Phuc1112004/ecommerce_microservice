package org.example.cartservice.service;

import org.example.cartservice.dto.CartItemRequestDTO;
import org.example.cartservice.dto.CartItemResponseDTO;

import java.util.List;

public interface CartItemService {
    CartItemResponseDTO addCartItem(CartItemRequestDTO request);
    List<CartItemResponseDTO> getItemsByCart(Long cartId);
    void removeCartItem(Long cartItemId);
}
