package org.example.inventoryservice.repository;


import org.example.inventoryservice.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findByPurchaseDateBetween(LocalDate start, LocalDate end, Pageable pageable);
}
