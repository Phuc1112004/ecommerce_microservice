//package org.example.orderservice.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.common.dto.kafka.OrderCreatedEvent;
//import org.example.paymentservice.enums.PaymentMethod;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/orders")
//@RequiredArgsConstructor
//public class OrderKafkaController {
//
//    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
//
//    // 🔹 API test Kafka
//    @GetMapping("/test")
//    public String testKafka() {
//        OrderCreatedEvent event = OrderCreatedEvent.builder()
//                .orderId(1L)
//                .userId(100L)
//                .totalAmount(250000L)
//                .paymentMethod(PaymentMethod.VNPAY)
//                .build();
//
//        kafkaTemplate.send("order-created", event);
//        return "✅ OrderCreatedEvent sent successfully!";
//    }
//
//    // 🔹 Gửi event thực khi đơn hàng được tạo (nếu bạn có hàm createOrder)
//    // @PostMapping
//    // public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO request) {
//    //     OrderResponseDTO order = orderService.createOrder(request);
//    //     OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getUserId(), order.getTotalAmount(), order.getPaymentMethod());
//    //     kafkaTemplate.send("order-created", event);
//    //     return ResponseEntity.ok(order);
//    // }
//}
