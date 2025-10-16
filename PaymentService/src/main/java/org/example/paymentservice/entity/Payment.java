package org.example.paymentservice.entity;



import jakarta.persistence.*;
import lombok.Data;
import org.example.paymentservice.enums.PaymentMethod;
import org.example.paymentservice.enums.PaymentStatus;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long orderId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // COD, VNPay, Momo, Paypal...
    private Long amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // pending, completed, failed
    private LocalDateTime paidAt;
}
