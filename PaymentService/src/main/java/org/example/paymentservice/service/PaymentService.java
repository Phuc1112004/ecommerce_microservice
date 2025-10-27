package org.example.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.client.OrderClient;
import org.example.common.dto.OrderInfoDTO;
import org.example.paymentservice.dto.PaymentRequestDTO;
import org.example.paymentservice.dto.PaymentResponseDTO;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.enums.PaymentMethod;
import org.example.paymentservice.enums.PaymentStatus;
import org.example.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

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
            orderClient.updateOrderStatus(orderId,"PAID");
        } else if ("FAILED".equals(status)) {
            payment.setStatus(PaymentStatus.FAILED);
            orderClient.updateOrderStatus(orderId,"FAILED");
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
//        payment.setPaymentMethod(convertMethod(event.getPaymentMethod()));
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
        dto.setPaymentMethod(payment.getPaymentMethod().toString());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus().toString());
        dto.setPaidAt(payment.getPaidAt());
        return dto;
    }
}

