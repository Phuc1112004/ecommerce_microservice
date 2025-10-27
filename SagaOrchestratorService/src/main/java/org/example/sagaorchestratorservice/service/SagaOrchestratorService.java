package org.example.sagaorchestratorservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.client.BookClient;
import org.example.common.client.OrderClient;
import org.example.common.client.PaymentClient;
import org.example.common.dto.BookInfoDTO;
import org.example.common.dto.OrderInfoDTO;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestratorService {

    private final OrderClient orderClient;
    private final PaymentClient paymentClient;
    private final BookClient bookClient;

    public void processOrder(Long orderId) {
        log.info("🧭 Start Saga for order {}", orderId);

        OrderInfoDTO order = orderClient.getOrderById(orderId);
        if (order == null) {
            log.error("❌ Order {} not found!", orderId);
            return;
        }

        try {
            // B1: Thanh toán
            log.info("💳 Processing payment for order {}", orderId);
            paymentClient.updatePaymentByOrder(orderId, "SUCCESS");

            // B2: Trừ stock
            log.info("📦 Updating stock for ordered items...");
            // Giả lập: lấy bookId và quantity từ order (demo bạn có thể fix cứng)
            Long bookId = 1L;
            bookClient.updateStockQuantity(bookId, -1);

            // B3: Hoàn tất
            orderClient.updateOrderStatus(orderId, "COMPLETED");
            log.info("✅ Saga success for order {}", orderId);

        } catch (Exception e) {
            log.error("⚠️ Saga failed for order {}, rolling back...", orderId);

            // Rollback logic
            paymentClient.updatePaymentByOrder(orderId, "FAILED");
            orderClient.updateOrderStatus(orderId, "CANCELLED");
            // Tùy chọn: bookClient.updateStockQuantity(bookId, +1);
        }
    }
}