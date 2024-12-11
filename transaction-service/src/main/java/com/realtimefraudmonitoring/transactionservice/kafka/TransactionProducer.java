package com.realtimefraudmonitoring.transactionservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public TransactionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    public void sendTransaction(String userId, String transactionJson) {
        logger.info("Producing message to topic: transaction.events, key: {}, payload: {}", userId, transactionJson);

        kafkaTemplate.send("transaction.events", userId, transactionJson).addCallback(
                result -> {
                    if (result != null) {
                        logger.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}, Key: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                userId);
                    }
                },
                ex -> logger.error("Failed to produce message to topic: transaction.events, key: {}, error: {}", userId, ex.getMessage(), ex)
        );
    }


}
