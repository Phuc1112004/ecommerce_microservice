package org.example.cartservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CartRequestDTO {
    private Long cartId;
    @NotNull(message = "Người dùng không được null")
    private Long userId;
    @NotEmpty(message = "Giỏ hàng phải có ít nhất một sản phẩm")
    @Valid
    private List<CartItemRequestDTO> listCartItems;
}
