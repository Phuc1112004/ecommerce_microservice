package org.example.orderservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/kafka")
@AllArgsConstructor
public class TestKafkaController {
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping()
    public String testKafka() {
        kafkaTemplate.send("order-created", "world");
        return "success";
    }
}
