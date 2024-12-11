package com.realtimefraudmonitoring.transactionservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {
    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);

//    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

//    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate, ObjectMapper objectMapper) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.objectMapper = objectMapper;
//    }

    public TransactionProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransaction(TransactionEvent transactionEvent) {
        try {
            // Serialize the transactionEvent to JSON
            String transactionJson = objectMapper.writeValueAsString(transactionEvent);
            // Log transaction details
            logger.info("Preparing to send transaction: {}", transactionEvent);
            String key = transactionEvent.getTransactionId(); // Partitioning by transactionId
            // Produce the event to the transaction.events topic
            kafkaTemplate.send("transaction.events", key, transactionJson).addCallback(
                    result -> {
                        if (result != null) {
                            logger.info("Message sent successfully. Topic: {}, Partition: {}, Offset: {}, Key: {}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    transactionEvent.getTransactionId());
                        }
                    },
                    ex -> logger.error("Failed to send transaction. Key: {}, Error: {}", transactionEvent.getTransactionId(), ex.getMessage(), ex)
            );

        } catch (Exception ex) {
            logger.error("Error serializing or sending transaction: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error sending transaction to Kafka", ex);
        }
    }


}
