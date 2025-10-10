package org.example.inventoryservice.controller;


import jakarta.validation.Valid;
import org.example.inventoryservice.dto.PurchaseRequestDTO;
import org.example.inventoryservice.dto.PurchaseResponseDTO;
import org.example.inventoryservice.service.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/inventory/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseResponseDTO> createPurchase(@RequestBody @Valid PurchaseRequestDTO request) {
        PurchaseResponseDTO createPurchase = purchaseService.createPurchase(request);
        return ResponseEntity.ok(createPurchase);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PurchaseResponseDTO>> getAllPurchases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("purchaseDate").descending());
        return ResponseEntity.ok(purchaseService.getAllPurchase(pageable));
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseResponseDTO> getBookById(@PathVariable Long id) {
        PurchaseResponseDTO book = purchaseService.getPurchaseById(id);
        if (book == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(book);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseResponseDTO> updateBook(@PathVariable Long id,
                                                      @RequestBody @Valid PurchaseRequestDTO request) {
        PurchaseResponseDTO updatedBook = purchaseService.updatePurchase(id, request);
        if (updatedBook == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedBook);
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        boolean deleted = purchaseService.deletePurchase(id);
        if (!deleted) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PurchaseResponseDTO>> search(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "purchaseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PurchaseResponseDTO> result = purchaseService.searchByDate(startDate, endDate, pageable);
        return ResponseEntity.ok(result);
    }

}
