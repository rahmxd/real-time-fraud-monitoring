package com.realtimefraudmonitoring.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.kafka.TransactionProducer;
import com.realtimefraudmonitoring.transactionservice.model.Transaction;
import com.realtimefraudmonitoring.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;
    private final ObjectMapper objectMapper;


    public TransactionService(TransactionRepository transactionRepository, TransactionProducer transactionProducer, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionProducer = transactionProducer;
        this.objectMapper = objectMapper;
    }

    public Transaction saveTransaction(TransactionEvent transactionEvent) throws Exception {
        // Map DTO to Entity
        Transaction transaction = mapToEntity(transactionEvent);

        // Save to the transactiondb database
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Map Entity back to DTO for Kafka
        TransactionEvent event = mapToDto(savedTransaction);

        // Convert transaction to JSON
        String transactionJson = objectMapper.writeValueAsString(event);

        // Publish to kafka now
        transactionProducer.sendTransaction(event.getUserId(), transactionJson);

        return savedTransaction;
    }

    private Transaction mapToEntity(TransactionEvent transactionEvent) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionEvent.getTransactionId());
        transaction.setUserId(transactionEvent.getUserId());
        transaction.setPaymentType(transactionEvent.getPaymentType());
        transaction.setAmount(transactionEvent.getAmount());
        transaction.setCurrency(transactionEvent.getCurrency());
        transaction.setStatus(transactionEvent.getStatus());
        return transaction;
    }

    private TransactionEvent mapToDto(Transaction transaction) {
        TransactionEvent dto = new TransactionEvent();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUserId());
        dto.setPaymentType(transaction.getPaymentType());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus());
        return dto;
    }

}
