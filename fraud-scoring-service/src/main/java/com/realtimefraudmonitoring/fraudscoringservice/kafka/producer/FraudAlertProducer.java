package com.realtimefraudmonitoring.fraudscoringservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FraudAlertProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public FraudAlertProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAlert(String userId, String alertJson) {
        kafkaTemplate.send("fraud.alerts", userId, alertJson);
    }
}
