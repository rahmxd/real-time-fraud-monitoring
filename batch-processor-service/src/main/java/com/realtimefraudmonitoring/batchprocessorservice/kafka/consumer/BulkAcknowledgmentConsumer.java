package com.realtimefraudmonitoring.batchprocessorservice.kafka.consumer;

import com.realtimefraudmonitoring.avro.BulkEvent;
import com.realtimefraudmonitoring.avro.TransactionEvent;
import com.realtimefraudmonitoring.batchprocessorservice.model.BulkEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransactionEntity;
import com.realtimefraudmonitoring.batchprocessorservice.repository.BulkRepository;
import com.realtimefraudmonitoring.batchprocessorservice.service.BatchBulkService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BulkAcknowledgmentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BulkAcknowledgmentConsumer.class);

    private final BatchBulkService batchBulkService;

    public BulkAcknowledgmentConsumer(BatchBulkService batchBulkService) {
        this.batchBulkService = batchBulkService;
    }

    @KafkaListener(
            topics = "bulk-scoring.acknowledgment",
            groupId = "batch-processor-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenAcknowledgment(String transactionId) {
        logger.info("Acknowledgment received for transaction ID: {}", transactionId);

        try {
            batchBulkService.handleBulkAcknowledgment(transactionId);
        } catch (Exception ex) {
            logger.error("Failed to handle acknowledgment for transaction ID: {}", transactionId, ex);
        }
    }
}

