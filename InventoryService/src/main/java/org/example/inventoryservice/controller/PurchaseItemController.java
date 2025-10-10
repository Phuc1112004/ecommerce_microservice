package org.example.inventoryservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.inventoryservice.dto.PurchaseItemRequestDTO;
import org.example.inventoryservice.dto.PurchaseItemResponseDTO;
import org.example.inventoryservice.service.PurchaseItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/purchase-items")
@RequiredArgsConstructor
public class PurchaseItemController {

    private final PurchaseItemService purchaseItemService;

    // ---------------- CREATE ----------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseItemResponseDTO> createPurchaseItem(
            @RequestBody @Valid PurchaseItemRequestDTO request) {
        PurchaseItemResponseDTO response = purchaseItemService.createPurchaseItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ---------------- READ (list by purchase) ----------------
    @GetMapping("/purchase/{purchaseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PurchaseItemResponseDTO>> getItemsByPurchase(
            @PathVariable Long purchaseId) {
        List<PurchaseItemResponseDTO> items = purchaseItemService.getItemsByPurchaseId(purchaseId);
        return ResponseEntity.ok(items);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseItemResponseDTO> updatePurchaseItem(
            @PathVariable Long id,
            @RequestBody @Valid PurchaseItemRequestDTO request) {
        PurchaseItemResponseDTO updated = purchaseItemService.updatePurchaseItem(id, request);
        return ResponseEntity.ok(updated);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePurchaseItem(@PathVariable Long id) {
        boolean deleted = purchaseItemService.deletePurchaseItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

