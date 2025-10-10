package org.example.inventoryservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseRequestDTO {
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String supplierName;                 // tên nhà cung cấp
    @NotNull(message = "Ngày nhập hàng không được null")
    private LocalDate purchaseDate;          // ngày nhập hàng
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<PurchaseItemRequestDTO> listPurchaseItems;  // danh sách sách + số lượng + giá nhập

}
