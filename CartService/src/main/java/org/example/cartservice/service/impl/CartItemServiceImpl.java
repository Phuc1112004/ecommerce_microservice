package org.example.cartservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cartservice.dto.CartItemRequestDTO;
import org.example.cartservice.dto.CartItemResponseDTO;
import org.example.cartservice.entity.Cart;
import org.example.cartservice.entity.CartItem;
import org.example.cartservice.repository.CartItemRepository;
import org.example.cartservice.repository.CartRepository;
import org.example.cartservice.service.CartItemService;
import org.example.common.client.BookClient;
import org.example.common.dto.BookInfoDTO;
import org.example.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final BookClient bookClient;


    public CartItemResponseDTO addCartItem(CartItemRequestDTO request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + request.getUserId()));


        CartItem cartItem = new CartItem();

        cartItem.setCartId(cart.getCartId());
        cartItem.setBookId(request.getBookId());

        cartItem.setQuantity(request.getQuantity());
        CartItem saved = cartItemRepository.save(cartItem);

        return mapToDTO(saved);
    }

    public List<CartItemResponseDTO> getItemsByCart(Long cartId) {
        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        if (items.isEmpty()) {
            throw new ResourceNotFoundException("No items found for cart id: " + cartId);
        }
        return items.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public void removeCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("CartItem not found");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    private CartItemResponseDTO mapToDTO(CartItem item) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        BookInfoDTO bookInfo = bookClient.getBookById(item.getBookId());

        dto.setCartItemId(item.getCartItemId());
        dto.setBookId(bookInfo.getBookId());
        dto.setBookTitle(bookInfo.getTitle());
        dto.setPrice(bookInfo.getSalePrice());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}

