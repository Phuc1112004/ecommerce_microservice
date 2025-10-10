package org.example.cartservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;



@SpringBootApplication(scanBasePackages = {
        "org.example.cartservice",
        "org.example.common"   // nếu bạn dùng FeignClient, DTO chung ở đây
})
@EnableFeignClients(basePackages = "org.example.common.client")
public class CartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }

}
