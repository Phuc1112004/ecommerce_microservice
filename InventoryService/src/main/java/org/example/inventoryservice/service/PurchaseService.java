package org.example.inventoryservice.service;

import org.example.inventoryservice.dto.PurchaseRequestDTO;
import org.example.inventoryservice.dto.PurchaseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PurchaseService {
    PurchaseResponseDTO createPurchase(PurchaseRequestDTO request);
    Page<PurchaseResponseDTO> getAllPurchase(Pageable pageable);
    PurchaseResponseDTO getPurchaseById(Long id);
    PurchaseResponseDTO updatePurchase(Long id, PurchaseRequestDTO request);
    boolean deletePurchase(Long id);
    Page<PurchaseResponseDTO> searchByDate(LocalDate start, LocalDate end, Pageable pageable);

}
