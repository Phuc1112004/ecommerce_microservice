package org.example.paymentservice.repository;


import org.example.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
//    Payment findByOrders(Orders orders);
    Optional<Payment> findByOrderId(Long orderId);
}
