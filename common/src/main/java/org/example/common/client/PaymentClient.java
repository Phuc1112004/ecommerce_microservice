package org.example.common.client;

import org.example.common.dto.PaymentInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", url = "http://localhost:8003/api/payments")
public interface PaymentClient {

    @GetMapping("/{paymentId}")
    PaymentInfoDTO getPaymentById(@PathVariable("paymentId") Long paymentId);

    @PutMapping("/{paymentId}/status")
    void updatePaymentStatus(@PathVariable("paymentId") Long paymentId,
                             @RequestParam("status") String status);

    @PutMapping("/update-by-order/{orderId}")
    void updatePaymentByOrder(@PathVariable("orderId") Long orderId,
                              @RequestParam("status") String status);
}

