package com.realtimefraudmonitoring.transactionservice.kafka;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.model.Transaction;
import com.realtimefraudmonitoring.transactionservice.repository.TransactionRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);
    private final TransactionRepository transactionRepository;

    public TransactionConsumer(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
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

        handleAcknowledgment(event);
    }

    private void handleAcknowledgment(TransactionEvent event) {
        // Idempotency Check
        Transaction transaction = transactionRepository.findByTransactionId(event.getTransactionId().toString());
        if (transaction == null) {
            logger.warn("Transaction with ID {} not found in the database", event.getTransactionId());
            return;
        }

        if (transaction.isAcknowledged()) {
            logger.info("Acknowledgment for transaction ID {} already processed. Skipping.", event.getTransactionId());
            return;
        }

        // Mark the transaction as acknowledged
        transaction.setAcknowledged(true);
        transactionRepository.save(transaction);
        logger.info("Transaction {} acknowledged and updated in the database", event.getTransactionId());
    }
}
