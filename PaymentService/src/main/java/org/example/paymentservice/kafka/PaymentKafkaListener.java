package org.example.paymentservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.kafka.OrderCreatedEvent;
import org.example.paymentservice.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("📩 Received OrderCreatedEvent from order-service: {}", event);

        // Gọi PaymentService để tạo payment tương ứng
        paymentService.createPaymentFromOrderEvent(event);
    }
}
