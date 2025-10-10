package org.example.cartservice.controller;


import jakarta.validation.Valid;
import org.example.cartservice.dto.CartItemRequestDTO;
import org.example.cartservice.dto.CartItemResponseDTO;
import org.example.cartservice.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public ResponseEntity<CartItemResponseDTO> addCartItem(@RequestBody @Valid CartItemRequestDTO request) {
        return ResponseEntity.ok(cartItemService.addCartItem(request));
    }

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItemResponseDTO>> getItemsByCart(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartItemService.getItemsByCart(cartId));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long cartItemId) {
        cartItemService.removeCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}

