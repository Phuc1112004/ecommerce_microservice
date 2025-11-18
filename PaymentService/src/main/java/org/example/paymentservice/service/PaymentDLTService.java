package org.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.client.OrderClient;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentDLTService {

    private final OrderClient orderClient;

    @KafkaListener(topics = "payment-success-dlt", groupId = "payment-dlt-group")
    public void handleDLT(PaymentSuccessEvent event) {
        log.error("Message rơi vào DLT, xử lý thủ công: {}", event);

        // Ví dụ: cập nhật đơn hàng sang FAILED
        orderClient.updateOrderStatus(event.getOrderId(), "FAILED");

        // Nếu muốn gửi cảnh báo:
        // alertService.notifyAdmin(event);
    }
}

