package org.example.paymentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.kafka.OrderCreatedEvent;
import org.example.common.dto.kafka.PaymentFailedEvent;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.enums.PaymentMethod;
import org.example.paymentservice.enums.PaymentStatus;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("💰 [PaymentService] Received OrderCreatedEvent: {}", event);

        try {
            // 1️⃣ Tạo bản ghi Payment trong DB
            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());
            payment.setAmount(event.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.VNPAY);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // 2️⃣ Giả lập thanh toán thành công (có thể gọi VNPay thật)
            boolean paid = true;
            if (paid) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);

                PaymentSuccessEvent successEvent = PaymentSuccessEvent.builder()
                        .orderId(event.getOrderId())
                        .amount(event.getTotalAmount())
                        .items(event.getItems()) // danh sách sản phẩm từ OrderCreatedEvent
                        .build();

                kafkaTemplate.send("payment-success", successEvent);
                log.info("✅ Sent PaymentSuccessEvent: {}", successEvent);
            } else {
                throw new RuntimeException("Simulated payment failure");
            }

        } catch (Exception e) {
            log.error("❌ Payment failed for order {}: {}", event.getOrderId(), e.getMessage());
            PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .reason(e.getMessage())
                    .build();

            kafkaTemplate.send("payment-failed", failedEvent);
        }
    }
}


