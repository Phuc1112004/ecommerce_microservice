package org.example.inventoryservice.service.impl;


import lombok.RequiredArgsConstructor;
import org.example.common.client.BookClient;
import org.example.common.exception.ResourceNotFoundException;
import org.example.inventoryservice.dto.PurchaseItemRequestDTO;
import org.example.inventoryservice.dto.PurchaseItemResponseDTO;
import org.example.inventoryservice.dto.PurchaseRequestDTO;
import org.example.inventoryservice.dto.PurchaseResponseDTO;
import org.example.inventoryservice.entity.Purchase;
import org.example.inventoryservice.entity.PurchaseItem;
import org.example.inventoryservice.repository.PurchaseItemRepository;
import org.example.inventoryservice.repository.PurchaseRepository;
import org.example.inventoryservice.service.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final BookClient bookClient;


    // ---------------- CREATE ----------------
    @Transactional
    public PurchaseResponseDTO createPurchase(PurchaseRequestDTO request) {
        // 1. Tạo Purchase
        Purchase purchase = new Purchase();
        purchase.setSupplierName(request.getSupplierName());
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setTotalCost(0L); // khởi tạo trước

        // Lưu trước để có purchaseId
        Purchase savedPurchase = purchaseRepository.save(purchase);

        long totalCost = 0L;

        if (request.getListPurchaseItems() != null && !request.getListPurchaseItems().isEmpty()) {
            for (PurchaseItemRequestDTO itemReq : request.getListPurchaseItems()) {
                PurchaseItem item = new PurchaseItem();
                item.setPurchaseId(savedPurchase.getPurchaseId()); // set parent thủ công
                item.setBookId(itemReq.getBookId());
                item.setQuantity(itemReq.getQuantity());
                item.setUnitPrice(itemReq.getUnitPrice());

                purchaseItemRepository.save(item); // lưu item riêng

                // Cập nhật stock
                bookClient.updateStockQuantity(itemReq.getBookId(), itemReq.getQuantity());

                totalCost += itemReq.getQuantity() * itemReq.getUnitPrice();
            }
        }

        // Cập nhật tổng tiền vào purchase
        savedPurchase.setTotalCost(totalCost);
        purchaseRepository.save(savedPurchase);

        return convertToDTO(savedPurchase);
    }



    // ---------------- READ ----------------
    public Page<PurchaseResponseDTO> getAllPurchase(Pageable pageable) {
        return purchaseRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public PurchaseResponseDTO getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
    }

    // ---------------- UPDATE ----------------
    @Transactional
    public PurchaseResponseDTO updatePurchase(Long id, PurchaseRequestDTO request) {
        return purchaseRepository.findById(id)
                .map(purchase -> {
                    purchase.setSupplierName(request.getSupplierName());
                    purchase.setPurchaseDate(request.getPurchaseDate());
                    return convertToDTO(purchaseRepository.save(purchase));
                }).orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
    }

    // ---------------- DELETE ----------------
    @Transactional
    public boolean deletePurchase(Long id) {
        if (!purchaseRepository.existsById(id)) return false;
        purchaseRepository.deleteById(id);
        return true;
    }

    // ---------------- CONVERT ----------------
    private PurchaseResponseDTO convertToDTO(Purchase purchase) {
        PurchaseResponseDTO dto = new PurchaseResponseDTO();
        dto.setPurchaseId(purchase.getPurchaseId());
        dto.setSupplierName(purchase.getSupplierName());
        dto.setPurchaseDate(purchase.getPurchaseDate());
        dto.setTotalCost(purchase.getTotalCost() != null ? purchase.getTotalCost() : 0L);

        // Lấy danh sách PurchaseItem từ repository
        List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(purchase.getPurchaseId());

        if (items != null && !items.isEmpty()) {
            List<PurchaseItemResponseDTO> itemDTOs = items.stream()
                    .map(item -> {
                        PurchaseItemResponseDTO iDto = new PurchaseItemResponseDTO();
                        iDto.setPurchaseItemId(item.getPurchaseItemId());
                        iDto.setBookId(item.getBookId());

                        // Lấy title từ BookService qua FeignClient
//                        BookDTO book = bookClient.getBookById(item.getBookId());
                        iDto.setBookTitle(bookClient.getTitleByBookId(item.getBookId()));

                        iDto.setQuantity(item.getQuantity());
                        iDto.setUnitPrice(item.getUnitPrice());
                        iDto.setSubtotal(item.getQuantity() * item.getUnitPrice());
                        return iDto;
                    })
                    .collect(Collectors.toList());

            dto.setListPurchaseItems(itemDTOs);
        }
        return dto;
    }

    // ---------------- SEARCH BY DATE WITH PAGING ----------------
    public Page<PurchaseResponseDTO> searchByDate(LocalDate start, LocalDate end, Pageable pageable) {
        if (start == null || end == null)
            throw new IllegalArgumentException("Start date và End date không được để trống");
        if (end.isBefore(start))
            throw new IllegalArgumentException("End date phải lớn hơn hoặc bằng Start date");

        Page<Purchase> pageResult = purchaseRepository.findByPurchaseDateBetween(start, end, pageable);
        if (pageResult.isEmpty())
            throw new ResourceNotFoundException("Không tìm thấy đơn nhập hàng trong khoảng thời gian này");

        return pageResult.map(this::convertToDTO);
    }
}
