package com.realtimefraudmonitoring.batchprocessorservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BatchProducer {

    private static final Logger logger = LoggerFactory.getLogger(BatchProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public BatchProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendBatch(String batchId, String batchJson) {
        logger.info("Producing batch message to topic: batch.events, key: {}, payload: {}", batchId, batchJson);

        kafkaTemplate.send("batch.events", batchId, batchJson).addCallback(
                result -> {
                    if (result != null) {
                        logger.info("Batch message sent successfully. Topic: {}, Partition: {}, Offset: {}, Key: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                batchId);
                    }
                },
                ex -> logger.error("Failed to produce batch message to topic: batch.events, key: {}, error: {}", batchId, ex.getMessage(), ex)
        );
    }


}
