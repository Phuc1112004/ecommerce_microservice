package org.example.cartservice.controller;


import jakarta.validation.Valid;
import org.example.cartservice.dto.CartRequestDTO;
import org.example.cartservice.dto.CartResponseDTO;
import org.example.cartservice.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartResponseDTO> createCart(@RequestBody @Valid CartRequestDTO request) {
        return ResponseEntity.ok(cartService.createCart(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartResponseDTO> getCartByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

//    @PostMapping("/checkout")
//    public ResponseEntity<Orders> checkout(
//            @RequestParam Long userId,
//            @RequestParam String shippingAddress) {
//        Orders order = cartService.checkout(userId, shippingAddress);
//        return ResponseEntity.ok(order);
//    }

}
