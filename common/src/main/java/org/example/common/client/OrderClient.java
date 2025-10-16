package org.example.common.client;

import org.example.common.dto.OrderInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "order-service", url = "http://localhost:8005/api/orders")
public interface OrderClient {
    @GetMapping("/{orderId}")
    OrderInfoDTO getOrderById(@PathVariable Long orderId);

    @PutMapping("/{orderId}/internal-status")
    void updateOrderStatus(@PathVariable Long orderId,
                           @RequestParam String status);

    @GetMapping("/{orderId}/info")
    OrderInfoDTO getOrderInfo(@PathVariable("orderId") Long orderId);

}
