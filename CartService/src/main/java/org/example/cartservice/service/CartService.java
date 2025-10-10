package org.example.cartservice.service;

import org.example.cartservice.dto.CartRequestDTO;
import org.example.cartservice.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO createCart(CartRequestDTO request);
    CartResponseDTO getCartByUser(Long userId);

}
