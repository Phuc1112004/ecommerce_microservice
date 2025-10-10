package org.example.catelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@EnableFeignClients(basePackages = {"org.example.common.client"})
@ComponentScan(basePackages = {"org.example.common","org.example.catelog"})
public class CatelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatelogApplication.class, args);
    }

}
