package org.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItemResquestDTO {
    @NotNull(message = "BookId không được để trống")
    private Long bookId;
    private Long orderId;
    private Long userId;
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
    @PositiveOrZero(message = "Giá phải lớn hơn hoặc bằng 0")
    private Long price;
}
