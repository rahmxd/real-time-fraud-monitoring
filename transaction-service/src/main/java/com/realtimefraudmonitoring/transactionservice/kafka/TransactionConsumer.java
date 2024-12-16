package com.realtimefraudmonitoring.transactionservice.kafka;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    @KafkaListener(
            topics = "fraud-scoring.acknowledgment",
            groupId = "transaction-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenAcknowledgment(ConsumerRecord<String, TransactionEvent> record) {
        TransactionEvent event = record.value();

        // Log the acknowledgment received
        logger.info("Acknowledgment received for transaction ID: {}, status: {}",
                event.getTransactionId(), event.getStatus());

        // Add acknowledgment logic here
        handleAcknowledgment(event);
    }

    private void handleAcknowledgment(TransactionEvent event) {
        // Example: Update transaction status in the database
        logger.info("Transaction {} acknowledged with status: {}", event.getTransactionId(), event.getStatus());

        // Implement database update logic or further processing here
    }
}
