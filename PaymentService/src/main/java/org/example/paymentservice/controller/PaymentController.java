package org.example.paymentservice.controller;


import lombok.RequiredArgsConstructor;
import org.example.paymentservice.dto.PaymentRequestDTO;
import org.example.paymentservice.dto.PaymentResponseDTO;
import org.example.paymentservice.service.PaymentService;
import org.example.paymentservice.service.VNPayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final VNPayService vnPayService;

    // 1. Tạo thanh toán (VNPay, Momo...)
    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequestDTO req) {
        String paymentUrl = String.valueOf(paymentService.createPayment(req));
        return ResponseEntity.ok(paymentUrl);
    }

    // 3. Lấy danh sách tất cả payment
    @GetMapping
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
