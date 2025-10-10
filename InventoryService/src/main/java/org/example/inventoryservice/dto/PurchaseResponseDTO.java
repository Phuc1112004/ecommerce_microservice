package org.example.inventoryservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseResponseDTO {
    private Long purchaseId;
    private String supplierName;
    private LocalDate purchaseDate;
    private Long totalCost;                       // tổng tiền nhập hàng
    private List<PurchaseItemResponseDTO> listPurchaseItems;  // chi tiết từng sách
}
