package org.example.cartservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.cartservice.dto.CartItemResponseDTO;
import org.example.cartservice.dto.CartRequestDTO;
import org.example.cartservice.dto.CartResponseDTO;
import org.example.cartservice.entity.Cart;
import org.example.cartservice.entity.CartItem;
import org.example.cartservice.repository.CartItemRepository;
import org.example.cartservice.repository.CartRepository;
import org.example.cartservice.service.CartService;
import org.example.common.client.BookClient;
import org.example.common.client.UserClient;
import org.example.common.dto.BookInfoDTO;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserClient userClient;
    private final BookClient bookClient;

//    private final OrderRepository orderRepository;


    public CartResponseDTO createCart(CartRequestDTO request) {
        // kiểm tra user có tồn tại không
        var user = userClient.getUserInfo(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Cart cart = new Cart();
        cart.setUserId(request.getUserId());
        Cart saved = cartRepository.save(cart);

        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(saved.getCartId());
        dto.setUserId(saved.getUserId());
        return dto;
    }

    public CartResponseDTO getCartByUser(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        CartResponseDTO dto = new CartResponseDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUserId());

        // Map CartItem -> CartItemResponseDTO
        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());
        List<CartItemResponseDTO> itemDTOs = items.stream()
                .map(item -> {
                    BookInfoDTO bookInfo = bookClient.getBookInfo(item.getBookId());

                    CartItemResponseDTO itemDTO = new CartItemResponseDTO();
                    itemDTO.setCartItemId(item.getCartItemId());
                    itemDTO.setBookId(item.getBookId());
                    itemDTO.setBookTitle(bookInfo.getTitle());
                    itemDTO.setPrice(bookInfo.getSalePrice());
                    itemDTO.setQuantity(item.getQuantity());
                    return itemDTO;
                })
                .collect(Collectors.toList());

        dto.setListCartItems(itemDTOs);
        return dto;
    }


//    @Transactional
//    public Orders checkout(Long userId, String shippingAddress) {
//        // 1. Lấy giỏ hàng của user
//        Cart cart = cartRepository.findByUser_UserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("Giỏ hàng trống"));
//
//        if (cart.getListCartItems().isEmpty()) {
//            throw new RuntimeException("Giỏ hàng rỗng, không thể thanh toán");
//        }
//
//        // 2. Tạo Orders
//        Orders order = new Orders();
//        order.setUsers(cart.getUser());
//        order.setShippingAddress(shippingAddress);
//        order.setCreatedAt(LocalDateTime.now());
//        order.setStatus(OrderStatus.PENDING);
//
//        long totalAmount = 0L;
//        List<OrderItem> orderItems = new ArrayList<>();
//
//        for (CartItem cartItem : cart.getListCartItems()) {
//            OrderItem oi = new OrderItem();
//            oi.setOrders(order);
//            oi.setBooks(cartItem.getBooks());
//            oi.setQuantity(cartItem.getQuantity());
//            oi.setPrice(cartItem.getBooks().getSalePrice());
//
//            totalAmount += cartItem.getBooks().getSalePrice() * cartItem.getQuantity();
//            orderItems.add(oi);
//        }
//
//        order.setTotalAmount(totalAmount);
//        order.setListOrderItems(orderItems);
//
//        // 3. Lưu Orders + OrderItem
//        Orders savedOrder = orderRepository.save(order);
//
//        // 4. Clear giỏ hàng
//        cartItemRepository.deleteAll(cart.getListCartItems());
//
//        return savedOrder;
//    }
}

