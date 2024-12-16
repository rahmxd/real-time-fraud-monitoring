package com.realtimefraudmonitoring.transactionservice.kafka;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProducer.class);
    private static final String TRANSACTION_TOPIC = "transaction.events";

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(TransactionEvent transactionEvent) {
        //get key but have to convert CharSequence to String
        String key = transactionEvent.getTransactionId().toString();
        logger.info("Sending transaction event with key: {}", key);

        kafkaTemplate.send(TRANSACTION_TOPIC, key, transactionEvent).addCallback(
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
    }
}
