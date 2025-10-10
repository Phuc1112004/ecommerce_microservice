package org.example.orderservice.repository;


import org.example.orderservice.entity.Orders;
import org.example.orderservice.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    @Override
    List<Orders> findAll();

    @Query("SELECT o FROM Orders o " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:dateFrom IS NULL OR o.createdAt >= :dateFrom) " +
            "AND (:dateTo IS NULL OR o.createdAt <= :dateTo)")
    List<Orders> searchOrders(@Param("status") OrderStatus status,
                              @Param("dateFrom") LocalDateTime dateFrom,
                              @Param("dateTo") LocalDateTime dateTo);
}
