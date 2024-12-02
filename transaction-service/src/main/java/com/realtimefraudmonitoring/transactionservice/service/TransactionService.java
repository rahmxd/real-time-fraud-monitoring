package com.realtimefraudmonitoring.transactionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public Transaction saveTransaction(Transaction transaction) throws Exception {
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Convert transaction to JSON
        String transactionJson = objectMapper.writeValueAsString(savedTransaction);

        // Publish to kafka now
        transactionProducer.sendTransaction(savedTransaction.getUserId(), transactionJson);

        return savedTransaction;
    }


}
