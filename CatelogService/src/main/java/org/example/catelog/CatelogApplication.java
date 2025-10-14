package org.example.catelog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableFeignClients(basePackages = {"org.example.common.client"})
@ComponentScan(basePackages = {"org.example.common","org.example.catelog"})
public class CatelogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatelogApplication.class, args);
    }

}
