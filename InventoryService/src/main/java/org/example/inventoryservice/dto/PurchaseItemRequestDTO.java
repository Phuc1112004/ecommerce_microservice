package org.example.inventoryservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PurchaseItemRequestDTO {
    private Long purchaseId;
    @NotNull(message = "Mã sách không được để trống")
    private Long bookId;       // id sách nhập
    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;    // số lượng nhập
    @NotNull(message = "Đơn giá không được để trống")
    @PositiveOrZero(message = "Đơn giá phải >= 0")
    private Long unitPrice;     // giá nhập tại thời điểm mua
}
