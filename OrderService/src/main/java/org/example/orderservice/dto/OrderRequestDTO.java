package org.example.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Long userId;  // người đặt hàng
    private String receiver;
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;   // địa chỉ ship
    @NotEmpty(message = "Đơn hàng phải có ít nhất một sản phẩm")
    @Valid
    private List<OrderItemResquestDTO> listItems;   // danh sách + số lượng
}
