package org.example.paymentservice.kafka;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.kafka.OrderCreatedEvent;
import org.example.common.dto.kafka.PaymentCompletedEvent;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.enums.PaymentMethod;
import org.example.paymentservice.enums.PaymentStatus;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderCreatedConsumer {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("📩 Received OrderCreatedEvent: " + event);

        // Tạo record payment
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getTotalAmount());
        payment.setPaymentMethod(PaymentMethod.VNPAY);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

//        // Giả lập thanh toán thành công
//        payment.setStatus(PaymentStatus.COMPLETED);
//        payment.setPaidAt(LocalDateTime.now());
//        paymentRepository.save(payment);
//
//        // Gửi PaymentCompletedEvent ngược lại OrderService
//        PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(
//                payment.getOrderId(),
//                payment.getPaymentId(),
//                payment.getStatus(),
//                payment.getPaidAt()
//        );
//
//        kafkaTemplate.send("payment-completed", completedEvent);
//        System.out.println("✅ Sent PaymentCompletedEvent: " + completedEvent);
    }
}
