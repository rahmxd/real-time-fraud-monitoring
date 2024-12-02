package com.realtimefraudmonitoring.fraudscoringservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.fraudscoringservice.dto.TransactionScoreEvent;
import com.realtimefraudmonitoring.fraudscoringservice.model.Transaction;
import com.realtimefraudmonitoring.fraudscoringservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.realtimefraudmonitoring.fraudscoringservice.kafka.producer.FraudAlertProducer;

@Service
public class FraudScoringService {

    private final TransactionRepository transactionRepository;
    private final FraudAlertProducer fraudAlertProducer;
    private final ObjectMapper objectMapper;

    public FraudScoringService(TransactionRepository transactionRepository, FraudAlertProducer fraudAlertProducer, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.fraudAlertProducer = fraudAlertProducer;
        this.objectMapper = objectMapper;
    }

    public void processTransaction(TransactionScoreEvent transactionScoreEvent) throws Exception {
        // Fraud scoring logic
        double fraudScore = calculateFraudScore(transactionScoreEvent);
        transactionScoreEvent.setFraudScore(fraudScore);

        // Map DTO to Entity
        Transaction transaction = mapToEntity(transactionScoreEvent);

        // Save to fraudscoringdb database
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Map Entity back to DTO for kafka
        // should it be an exclusive dto?
        TransactionScoreEvent event = mapToDto(savedTransaction);

        // Convert transaction to JSON
        String transactionJson = objectMapper.writeValueAsString(event);

        // Publish to alerts topic if fraud score is high
        if (fraudScore > 80) {
            String alertMessage = objectMapper.writeValueAsString(transaction);
            fraudAlertProducer.sendAlert(transactionScoreEvent.getUserId(), alertMessage);
        }
    }

    private double calculateFraudScore(TransactionScoreEvent transaction) {
        // Simple scoring logic for demonstration
        double score = 0;
        if (transaction.getAmount() > 10000) score += 50;
        if ("USD".equalsIgnoreCase(transaction.getCurrency())) score += 20;
        return Math.min(score, 100);
    }

    private Transaction mapToEntity(TransactionScoreEvent transactionScoreEvent) {
        Transaction transactionEntity = new Transaction();
        transactionEntity.setUserId(transactionScoreEvent.getUserId());
        transactionEntity.setTransactionType(transactionScoreEvent.getTransactionType());
        transactionEntity.setAmount(transactionScoreEvent.getAmount());
        transactionEntity.setCurrency(transactionScoreEvent.getCurrency());
        transactionEntity.setStatus(transactionScoreEvent.getStatus());
        transactionEntity.setFraudScore(transactionScoreEvent.getFraudScore());
        return transactionEntity;
    }

    private TransactionScoreEvent mapToDto(Transaction transaction) {
        TransactionScoreEvent transactionEvent = new TransactionScoreEvent();
        transactionEvent.setUserId(transaction.getUserId());
        transactionEvent.setTransactionType(transaction.getTransactionType());
        transactionEvent.setAmount(transaction.getAmount());
        transactionEvent.setCurrency(transaction.getCurrency());
        transactionEvent.setStatus(transaction.getStatus());
        transactionEvent.setFraudScore(transaction.getFraudScore());
        return transactionEvent;
    }
}
