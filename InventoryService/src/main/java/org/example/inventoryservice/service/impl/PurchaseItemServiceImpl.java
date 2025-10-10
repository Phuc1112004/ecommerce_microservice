package org.example.inventoryservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.example.common.client.BookClient;
import org.example.inventoryservice.dto.PurchaseItemRequestDTO;
import org.example.inventoryservice.dto.PurchaseItemResponseDTO;
import org.example.inventoryservice.entity.Purchase;
import org.example.inventoryservice.entity.PurchaseItem;
import org.example.inventoryservice.repository.PurchaseItemRepository;
import org.example.inventoryservice.repository.PurchaseRepository;
import org.example.inventoryservice.service.PurchaseItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;
    private final PurchaseRepository purchaseRepository;
    private final BookClient bookClient;

    // ---------------- CREATE ----------------
    public PurchaseItemResponseDTO createPurchaseItem(PurchaseItemRequestDTO request) {
        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        PurchaseItem item = new PurchaseItem();
        item.setPurchaseId(purchase.getPurchaseId());
        item.setBookId(request.getBookId());

        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());

        PurchaseItem saved = purchaseItemRepository.save(item);
        return convertToDTO(saved);
    }

    // ---------------- READ ----------------
    public List<PurchaseItemResponseDTO> getItemsByPurchaseId(Long purchaseId) {
        return purchaseItemRepository.findByPurchaseId(purchaseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ---------------- UPDATE ----------------
    public PurchaseItemResponseDTO updatePurchaseItem(Long id, PurchaseItemRequestDTO request) {
        return purchaseItemRepository.findById(id)
                .map(item -> {

                    item.setBookId(request.getBookId());
                    item.setQuantity(request.getQuantity());
                    item.setUnitPrice(request.getUnitPrice());
                    PurchaseItem updated = purchaseItemRepository.save(item);
                    return convertToDTO(updated);
                }).orElseThrow(() -> new RuntimeException("PurchaseItem not found"));
    }

    // ---------------- DELETE ----------------
    public boolean deletePurchaseItem(Long id) {
        if (!purchaseItemRepository.existsById(id)) return false;
        purchaseItemRepository.deleteById(id);
        return true;
    }

    // ---------------- CONVERT ----------------
    private PurchaseItemResponseDTO convertToDTO(PurchaseItem item) {
        PurchaseItemResponseDTO dto = new PurchaseItemResponseDTO();
        dto.setPurchaseItemId(item.getPurchaseItemId());
        dto.setBookId(item.getBookId());

        dto.setBookTitle(bookClient.getTitleByBookId(item.getBookId()));

        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getQuantity() * item.getUnitPrice());
        return dto;
    }
}

