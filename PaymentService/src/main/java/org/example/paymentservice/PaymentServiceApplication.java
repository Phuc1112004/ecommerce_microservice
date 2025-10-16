package org.example.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication(scanBasePackages = {
        "org.example.paymentservice",
        "org.example.common"   // nếu bạn dùng FeignClient, DTO chung ở đây
})
@EnableFeignClients(basePackages = "org.example.common.client")
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
