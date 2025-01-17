package com.realtimefraudmonitoring.batchprocessorservice.kafka.producer;

import com.realtimefraudmonitoring.avro.BulkEvent;
import com.realtimefraudmonitoring.avro.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BulkProducer {

    private static final Logger logger = LoggerFactory.getLogger(BulkProducer.class);

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public BulkProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(TransactionEvent transactionEvent) {
        transactionEvent.setSource("batch-processor-service");
        String key = transactionEvent.getTransactionId().toString();
        logger.info("Producing bulk message to topic: bulk.transactions, bulkId: {}", key);
        kafkaTemplate.send("bulk.transactions", key, transactionEvent).addCallback(
                result -> {
                    if (result != null) {
                        logger.info("Transaction event sent successfully. TransactionId: {}, Partition: {}, Offset: {}",
                                transactionEvent.getTransactionId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                },
                ex -> logger.error("Producer: Failed to send transaction event message. TransactionId: {}, Error: {}", key, ex.getMessage())
        );
    }
}
