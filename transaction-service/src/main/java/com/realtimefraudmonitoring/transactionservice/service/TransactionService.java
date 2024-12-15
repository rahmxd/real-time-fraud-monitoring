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

    public TransactionService(TransactionRepository transactionRepository, TransactionProducer transactionProducer) {
        this.transactionRepository = transactionRepository;
        this.transactionProducer = transactionProducer;
    }

    public TransactionEvent saveTransaction(TransactionEvent transactionEvent) throws Exception {
        // Map DTO to Entity
        Transaction transaction = mapToTransactionEntity(transactionEvent);

        // Save to the transactiondb database
        Transaction savedTransaction = transactionRepository.save(transaction);

        // Map Entity back to DTO for Kafka
        TransactionEvent event = mapToTransactionEvent(savedTransaction);

        // Convert transaction to JSON
        // kafkaTemplate is configured to wrok with TransactionEvent objects directly using JSON serializer.
        // no need to manually serialize the object to JSON in the service layer.
//        String transactionJson = objectMapper.writeValueAsString(event);

        // Publish to kafka now
        transactionProducer.sendTransaction(event);

        return event;
    }


    private Transaction mapToTransactionEntity(TransactionEvent transactionEvent) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionEvent.getTransactionId());
        transaction.setUserId(transactionEvent.getUserId());
        transaction.setPaymentType(transactionEvent.getPaymentType());
        transaction.setAmount(transactionEvent.getAmount());
        transaction.setCurrency(transactionEvent.getCurrency());
        transaction.setStatus(transactionEvent.getStatus());
        transaction.setBulkId(transactionEvent.getBulkId());
        transaction.setBatchId(transactionEvent.getBatchId());
        return transaction;
    }

    private TransactionEvent mapToTransactionEvent(Transaction transaction) {
        TransactionEvent dto = new TransactionEvent();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUserId());
        dto.setPaymentType(transaction.getPaymentType());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus());
        dto.setBulkId(transaction.getBulkId());
        dto.setBatchId(transaction.getBatchId());
        return dto;
    }

}
