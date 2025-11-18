package org.example.orderservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.dto.kafka.PaymentFailedEvent;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.example.orderservice.enums.OrderStatus;
import org.example.orderservice.event.StockFailedEvent;
import org.example.orderservice.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener { // có thể tách ra riêng nếu muốn

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-success", groupId = "order-group")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        System.out.println("✅ [OrderService] Received PaymentSuccessEvent: " + event);

        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            System.out.println("📦 Order " + event.getOrderId() + " marked as PAID");
        });
    }

    @KafkaListener(topics = "payment-failed", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        System.out.println("❌ [OrderService] Payment failed: " + event.getReason());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "stock-failed", groupId = "order-group")
    public void handleStockFailed(StockFailedEvent event) {
        System.out.println("❌ [OrderService] Stock deduction failed: " + event.getReason());
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

}

