package com.realtimefraudmonitoring.transactionservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);
    private static final String TRANSACTION_TOPIC = "transaction.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransaction(TransactionEvent transactionEvent) {
        try {
            String payload = objectMapper.writeValueAsString(transactionEvent);
            String key = transactionEvent.getTransactionId();
            logger.info("Sending transaction event with key: {}", key);

            kafkaTemplate.send(TRANSACTION_TOPIC, key, payload).addCallback(
                    result -> {
                        if (result != null) {
                            logger.info("Message sent. Topic: {}, Partition: {}, Offset: {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    },
                    ex -> logger.error("Failed to send transaction with key: {}", key, ex)
            );
        } catch (Exception ex) {
            logger.error("Error serializing or sending transaction: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error sending transaction to Kafka", ex);
        }
    }
}
