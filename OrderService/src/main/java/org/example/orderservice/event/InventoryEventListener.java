package org.example.orderservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.kafka.InventoryFailedEvent;
import org.example.common.dto.kafka.InventorySuccessEvent;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryEventListener {

    private final OrderRepository orderRepository;

    /**
     * 🟢 Nhận event giảm kho thành công -> cập nhật đơn hàng thành COMPLETED
     */
    @KafkaListener(topics = "inventory-success", groupId = "order-group")
    public void handleInventorySuccess(InventorySuccessEvent event) {
        log.info("✅ [OrderService] Nhận InventorySuccessEvent cho orderId={}", event.getOrderId());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            log.info("🎉 Đơn hàng {} được cập nhật trạng thái COMPLETED", order.getOrderId());
        });
    }

    /**
     * ❌ Nhận event giảm kho thất bại -> cập nhật đơn hàng thành CANCELLED
     */
    @KafkaListener(topics = "inventory-failed", groupId = "order-group")
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.warn("⚠️ [OrderService] Nhận InventoryFailedEvent cho orderId={} | Lý do: {}",
                event.getOrderId(), event.getReason());

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.warn("🛑 Đơn hàng {} được cập nhật trạng thái CANCELLED", order.getOrderId());
        });
    }
}
