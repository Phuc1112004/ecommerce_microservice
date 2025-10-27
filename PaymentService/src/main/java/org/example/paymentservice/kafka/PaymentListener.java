package org.example.paymentservice.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.kafka.OrderCreatedEvent;
import org.example.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentService paymentService;

    // Lắng nghe event order được tạo từ OrderService
    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📥 Nhận OrderCreatedEvent từ Kafka: {}", event);
        try {
            paymentService.createPaymentFromOrderEvent(event);
        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý OrderCreatedEvent: {}", e.getMessage());
        }
    }
}

