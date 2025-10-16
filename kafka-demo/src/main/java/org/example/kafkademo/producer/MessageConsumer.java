package org.example.kafkademo.producer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @KafkaListener(topics = "${app.topic}", groupId = "demo-group")
    public void listen(String message) {
        System.out.println("📩 Received message: " + message);
    }
}

