package org.example.catalog.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.catalog.service.BookService;
import org.example.common.dto.kafka.InventorySuccessEvent;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventListener {

    private final BookService bookService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Lắng nghe sự kiện thanh toán thành công từ PaymentService
     */
    @KafkaListener(topics = "payment-success", groupId = "catalog-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("📦 [CatalogService] Received PaymentSuccessEvent for orderId={}", event.getOrderId());

        try {
            // ⚙️ Giả lập trừ tồn kho theo danh sách sản phẩm trong order
            // => Nếu event PaymentSuccessEvent có danh sách item (bookId, quantity), duyệt qua để trừ
            event.getItems().forEach(item -> {

                // Lấy thông tin book hiện tại
                var book = bookService.findBookById(item.getBookId())
                        .orElseThrow(() -> new RuntimeException("Book not found: " + item.getBookId()));

                // Check tồn kho
                if (book.getStockQuantity() < item.getQuantity()) {
                    throw new RuntimeException("Not enough stock for bookId=" + item.getBookId());
                }

                log.info("🔽 Reducing stock for bookId={} by {}", item.getBookId(), item.getQuantity());
                bookService.updateStockQuantity(item.getBookId(), -item.getQuantity());
            });

            // ✅ Gửi event thông báo thành công
            InventorySuccessEvent successEvent = InventorySuccessEvent.builder()
                    .orderId(event.getOrderId())
                    .message("Inventory reduced successfully")
                    .build();

            kafkaTemplate.send("inventory-success", successEvent);
            log.info("✅ Sent InventorySuccessEvent: {}", successEvent);

        } catch (Exception e) {
            log.error("❌ Failed to reduce inventory: {}", e.getMessage());

            // ❌ Gửi event thất bại để rollback Order
            throw e;
        }
    }
}

