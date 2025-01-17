package com.realtimefraudmonitoring.batchprocessorservice.kafka.producer;

import com.realtimefraudmonitoring.avro.BatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BatchProducer {

    private static final Logger logger = LoggerFactory.getLogger(BatchProducer.class);

    private final KafkaTemplate<String, BatchEvent> kafkaTemplate;

    public BatchProducer(KafkaTemplate<String, BatchEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBatch(BatchEvent batchEvent) {
        logger.info("Producing batch message to topic: batch.split.events, batchId: {}", batchEvent.getBatchId());
        String key = batchEvent.getBatchId().toString();
        // Add source to all transactions in the batch
        batchEvent.getTransactions().forEach(tx -> tx.setSource("batch-processor-service"));
        kafkaTemplate.send("batch.split.events", key, batchEvent).addCallback(
                result -> {
                    if (result != null) {
                        logger.info("Batch message sent successfully. Topic: {}, Partition: {}, Offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                },
                ex -> logger.error("Failed to produce batch message. BatchId: {}, Error: {}", batchEvent.getBatchId(), ex.getMessage())
        );
    }
}
