package org.example.orderservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
@Slf4j
public class KafkaService {
    KafkaTemplate<String, Object> kafkaSendEmail;
    KafkaTemplate<String, Object> kafkaPushNotify;

    public void sendEmail(Object data, String topic) {
        try {
            kafkaSendEmail.send(topic, data);
        } catch (Exception e) {
            log.error("send kafka fail");
        }
    }

    public void pushNotify(Object data) {
        try {
            kafkaPushNotify.send("pushNotify", data);
        } catch (Exception e) {
            log.error("send kafka fail");
        }
    }
}