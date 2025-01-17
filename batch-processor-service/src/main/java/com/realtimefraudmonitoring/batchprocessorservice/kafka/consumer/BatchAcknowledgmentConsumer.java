package com.realtimefraudmonitoring.batchprocessorservice.kafka.consumer;

import com.realtimefraudmonitoring.avro.BatchEvent;
import com.realtimefraudmonitoring.batchprocessorservice.service.BatchBulkService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BatchAcknowledgmentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BatchAcknowledgmentConsumer.class);

    private final BatchBulkService batchBulkService;

    public BatchAcknowledgmentConsumer(BatchBulkService batchBulkService) {
        this.batchBulkService = batchBulkService;
    }

    @KafkaListener(
            topics = "batch-scoring.acknowledgment",
            groupId = "batch-processor-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenAcknowledgment(ConsumerRecord<String, BatchEvent> record) {
        BatchEvent event = record.value();

        // Log the acknowledgment received
        logger.info("Acknowledgment received for batch ID: {}",
                event.getBatchId());

        batchBulkService.handleBatchAcknowledgment(event);
    }

}
