package org.example.paymentservice.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.common.client.OrderClient;
import org.example.common.dto.OrderInfoDTO;
import org.example.common.dto.kafka.OrderItemDTO;
import org.example.common.dto.kafka.PaymentSuccessEvent;
import org.example.paymentservice.entity.Payment;
import org.example.paymentservice.service.PaymentService;
import org.example.paymentservice.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vnpay")
public class VNPayController {

    private final VNPayService vnpayService;
    private final PaymentService paymentService;
    private final OrderClient orderClient;


//    private final OrderService orderService;

    @PostMapping("/create-payment/{orderId}")
    public String createPayment(@PathVariable Long orderId, HttpServletRequest request) throws Exception {

        OrderInfoDTO order = orderClient.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found with id = " + orderId);
        }

        paymentService.createOrGetVnpayPayment(orderId);

        String orderInfo = "Thanh toán đơn hàng #" + orderId;
        String ipAddr = request.getRemoteAddr();
        if (ipAddr == null || ipAddr.isEmpty() || ipAddr.equals("0:0:0:0:0:0:0:1")) ipAddr = "127.0.0.1";

        return vnpayService.createPaymentUrl(orderId, order.getTotalAmount(), ipAddr, orderInfo);
    }

    @GetMapping("/return")
    public ResponseEntity<String> vnpayReturn(HttpServletRequest request) throws Exception {
        System.out.println("Callback VNPAY reached VNPayController");
        Map<String, String> params = new HashMap<>();
        for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
            String key = en.nextElement();
            params.put(key, request.getParameter(key));
        }

        System.out.println("vnp_TxnRef = [" + params.get("vnp_TxnRef") + "]");

        // Loại bỏ hash trước khi kiểm tra chữ ký
        String vnpSecureHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        Long orderId = Long.parseLong(params.get("vnp_TxnRef"));
        String responseCode = params.get("vnp_ResponseCode");

        Payment payment = paymentService.getPaymentByOrderId(orderId); // tạo phương thức getPaymentByOrderId nếu chưa có

        if ("00".equals(responseCode)) {
            // 1️⃣ Cập nhật trạng thái thanh toán trước
            paymentService.markPaymentCompleted(payment);

            // 2️⃣ Trả về ngay cho VNPAY
            ResponseEntity<String> response = ResponseEntity.ok("Payment success");

            // 3️⃣ Gửi event Kafka bất đồng bộ
            CompletableFuture.runAsync(() -> {
                try {
                    // gọi endpoint nội bộ OrderService, không cần token
                    OrderInfoDTO orderInfo = orderClient.getInternalOrderInfo(orderId);
                    List<OrderItemDTO> items = orderInfo.getItems();
                    if (items == null) {
                        System.out.println("⚠️ Warning: orderInfo.items is null for orderId=" + orderId);
                        items = new ArrayList<>();
                    }

                    PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                            .orderId(orderId)
                            .userId(payment.getUserId())
                            .amount(payment.getAmount())
                            .items(items) // map lại nếu cần
                            .build();

                    System.out.println("📤 [PaymentService] Sending PaymentSuccessEvent: " + event);

                    paymentService.sendPaymentSuccessEvent(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return response;
        } else {
            paymentService.markPaymentFailed(payment);
            return ResponseEntity.ok("Payment failed");
        }
    }


}

