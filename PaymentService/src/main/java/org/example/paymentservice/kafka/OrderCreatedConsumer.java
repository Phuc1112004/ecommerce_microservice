//package org.example.paymentservice.kafka;
//
//import org.example.common.dto.kafka.OrderCreatedEvent;
//import org.example.common.dto.kafka.PaymentCompletedEvent;
//import org.example.common.dto.kafka.PaymentFailedEvent;
//import org.example.paymentservice.entity.Payment;
//import org.example.paymentservice.enums.PaymentStatus;
//import org.example.paymentservice.repository.PaymentRepository;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//
//@Service
//@RequiredArgsConstructor
//public class OrderCreatedConsumer {
//
//    private final PaymentRepository paymentRepository;
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    @KafkaListener(topics = "order-created", groupId = "payment-service")
//    public void handleOrderCreated(OrderCreatedEvent event) {
//        System.out.println("📩 Received OrderCreatedEvent: " + event);
//
//        try {
//            // 1. Tạo bản ghi thanh toán
//            Payment payment = new Payment();
//            payment.setOrderId(event.getOrderId());
//            payment.setAmount(event.getTotalAmount());
//            payment.setStatus(PaymentStatus.PENDING);
//            paymentRepository.save(payment);
//
//            // 2. Giả lập thanh toán thành công
//            Thread.sleep(1000); // mô phỏng call API
//            payment.setStatus(PaymentStatus.COMPLETED);
//            paymentRepository.save(payment);
//
//            // 3. Gửi event thành công
//            PaymentCompletedEvent successEvent = new PaymentCompletedEvent(
//                    event.getOrderId(),
//                    event.getUserId(),
//                    event.getTotalAmount()
//            );
//            kafkaTemplate.send("payment-success", successEvent);
//            System.out.println("✅ Payment success, sent PaymentCompletedEvent");
//
//        } catch (Exception e) {
//            // 4. Gửi event thất bại
//            PaymentFailedEvent failedEvent = new PaymentFailedEvent(
//                    event.getOrderId(),
//                    event.getUserId(),
//                    event.getTotalAmount()
//            );
//            kafkaTemplate.send("payment-failed", failedEvent);
//            System.out.println("❌ Payment failed, sent PaymentFailedEvent");
//        }
//    }
//}
