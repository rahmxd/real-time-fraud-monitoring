package com.realtimefraudmonitoring.fraudscoringservice.kafka.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.fraudscoringservice.dto.TransactionScoreEvent;
import com.realtimefraudmonitoring.fraudscoringservice.model.Transaction;
import com.realtimefraudmonitoring.fraudscoringservice.service.FraudScoringService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumer {

    private final FraudScoringService fraudScoringService;
    private final ObjectMapper objectMapper;

    public TransactionConsumer(FraudScoringService fraudScoringService) {
        this.fraudScoringService = fraudScoringService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "transaction.events", groupId = "fraud-scoring-group")
    public void consumeTransaction(String message) {
        try {
            TransactionScoreEvent transaction = objectMapper.readValue(message, TransactionScoreEvent.class);
            fraudScoringService.processTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
