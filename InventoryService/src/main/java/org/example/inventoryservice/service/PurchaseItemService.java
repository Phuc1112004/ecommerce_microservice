package org.example.inventoryservice.service;

import org.example.inventoryservice.dto.PurchaseItemRequestDTO;
import org.example.inventoryservice.dto.PurchaseItemResponseDTO;

import java.util.List;

public interface PurchaseItemService {
    PurchaseItemResponseDTO createPurchaseItem(PurchaseItemRequestDTO request);
    List<PurchaseItemResponseDTO> getItemsByPurchaseId(Long purchaseId);
    PurchaseItemResponseDTO updatePurchaseItem(Long id, PurchaseItemRequestDTO request);
    boolean deletePurchaseItem(Long id);
}
