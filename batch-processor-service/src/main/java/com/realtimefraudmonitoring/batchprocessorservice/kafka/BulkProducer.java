package com.realtimefraudmonitoring.batchprocessorservice.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BulkProducer {

    private static final Logger logger = LoggerFactory.getLogger(BatchProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public BulkProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendBulk(String bulkId, String bulkJson) {
        logger.info("Producing bulk message to topic: bulk.events, key: {}, payload: {}", bulkId, bulkJson);

        kafkaTemplate.send("bulk.events", bulkId, bulkJson).addCallback(
                result -> {
                    if (result != null) {
                        logger.info("Bulk message sent successfully. Topic: {}, Partition: {}, Offset: {}, Key: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                bulkId);
                    }
                },
                ex -> logger.error("Failed to produce bulk message to topic: bulk.events, key: {}, error: {}", bulkId, ex.getMessage(), ex)
        );
    }

}
