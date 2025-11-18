package org.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.client.OrderClient;
import org.example.common.dto.OrderInfoDTO;
import org.example.common.dto.kafka.OrderItemDTO;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.example.paymentservice.dto.PaymentRequestDTO;
import org.example.paymentservice.dto.PaymentResponseDTO;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.enums.PaymentMethod;
import org.example.paymentservice.enums.PaymentStatus;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final OrderClient orderClient;
//    private final OrderRepository orderRepository;
//    private final OrderService orderService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    public List<PaymentResponseDTO> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        System.out.println("PaymentRequestDTO.paymentMethod = [" + request.getPaymentMethod() + "]");

        OrderInfoDTO order = orderClient.getOrderById(request.getOrderId());
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getOrderId());
        payment.setUserId(order.getUserId());

        // Xử lý PaymentMethod an toàn
        String methodStr = request.getPaymentMethod();
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(methodStr.toUpperCase()); // chuyển về chữ hoa
        } catch (Exception e) {
            paymentMethod = PaymentMethod.VNPAY; // default nếu không hợp lệ
        }
        payment.setPaymentMethod(paymentMethod);

        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING); // mặc định pending
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        return convertToResponseDTO(saved);
    }


    public void updatePaymentStatus(Long orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if ("COMPLETED".equals(status)) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());

            // --- Gọi endpoint nội bộ của OrderService ---
            OrderInfoDTO orderInfo = orderClient.getInternalOrderInfo(orderId);

            // Chỉ gửi event, không cập nhật orderClient.updateOrderStatus
            PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                    .orderId(orderId)
                    .userId(payment.getUserId())
                    .amount(orderInfo.getTotalAmount())
                    .items(orderInfo.getItems()) // map lại nếu cần
                    .build();

            kafkaTemplate.send("payment-success", event);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            // Không gọi update order nữa
        }

        paymentRepository.save(payment);
    }


    public PaymentResponseDTO savePayment(Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponseDTO(savedPayment);
    }

    public PaymentResponseDTO createOrGetVnpayPayment(Long orderId) {

        OrderInfoDTO order = orderClient.getOrderById(orderId);

        if (order == null) {
            throw new RuntimeException("Order not found for ID: " + orderId);
        }
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setOrderId(orderId);
                    p.setAmount(order.getTotalAmount());
                    p.setStatus(PaymentStatus.PENDING);
                    p.setPaymentMethod(PaymentMethod.VNPAY); // chỉ VNPAY
                    return paymentRepository.save(p);
                });
        return convertToResponseDTO(payment);
    }


    // Tạo thanh toán tự động khi nhận event từ order-service
    public void createPaymentFromOrderEvent(org.example.common.dto.kafka.OrderCreatedEvent event) {
        log.info("➡ Creating payment record for orderId = {}", event.getOrderId());

        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getTotalAmount().longValue());
        payment.setStatus(PaymentStatus.PENDING);
        // Map enum từ common sang payment-service
        org.example.common.enums.PaymentMethod eventMethod = event.getPaymentMethod();
        org.example.paymentservice.enums.PaymentMethod paymentMethod =
                eventMethod != null
                        ? org.example.paymentservice.enums.PaymentMethod.valueOf(eventMethod.name())
                        : org.example.paymentservice.enums.PaymentMethod.VNPAY;

        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);
        log.info("✅ Payment created for order {}", event.getOrderId());
    }

    private PaymentMethod convertMethod(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            return PaymentMethod.VNPAY; // fallback
        }
    }



    private PaymentResponseDTO convertToResponseDTO(Payment payment) {
        PaymentResponseDTO dto = new PaymentResponseDTO();

        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrderId());

        // ✅ Tránh NullPointerException với enum
        dto.setPaymentMethod(payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : "UNKNOWN");
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "UNKNOWN");

        dto.setAmount(payment.getAmount() != null ? payment.getAmount() : 0L);
        dto.setPaidAt(payment.getPaidAt() != null ? payment.getPaidAt() : null);

        return dto;
    }


    // Lấy payment theo orderId
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId=" + orderId));
    }

    // Cập nhật trạng thái completed
    public void markPaymentCompleted(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    // Cập nhật trạng thái failed
    public void markPaymentFailed(Payment payment) {
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    // Gửi Kafka event PaymentSuccessEvent
    public void sendPaymentSuccessEvent(PaymentSuccessEvent event) {
        log.info("📤 Sending PaymentSuccessEvent to Kafka for orderId={}", event.getOrderId());

        try {
            kafkaTemplate.send("payment-success", event);
            log.info("✅ Event sent (fire-and-forget) for orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("❌ Failed to send PaymentSuccessEvent for orderId={}", event.getOrderId(), e);
        }
    }

}

