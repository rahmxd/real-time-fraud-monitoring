package com.realtimefraudmonitoring.batchprocessorservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BatchEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BulkEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.batchprocessorservice.kafka.BatchProducer;
import com.realtimefraudmonitoring.batchprocessorservice.kafka.BulkProducer;
import com.realtimefraudmonitoring.batchprocessorservice.model.BatchTransaction;
import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransaction;
import com.realtimefraudmonitoring.batchprocessorservice.repository.BatchRepository;
import com.realtimefraudmonitoring.batchprocessorservice.repository.TransactionRepository;
//import com.realtimefraudmonitoring.transactionservice.dto.BatchEvent;
//import com.realtimefraudmonitoring.transactionservice.dto.BulkEvent;
//import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
//import com.realtimefraudmonitoring.transactionservice.kafka.TransactionProducer;
//import com.realtimefraudmonitoring.transactionservice.model.BatchTransaction;
//import com.realtimefraudmonitoring.transactionservice.model.Transaction;
//import com.realtimefraudmonitoring.transactionservice.repository.BatchTransactionRepository;
//import com.realtimefraudmonitoring.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BatchBulkService {



    private final BatchRepository batchRepository;

    private final TransactionRepository transactionRepository;

    private final BatchProducer batchProducer;

    private final BulkProducer bulkProducer;
    private final ObjectMapper objectMapper;

    public BatchBulkService(BatchRepository batchRepository, TransactionRepository transactionRepository, BatchProducer batchProducer, BulkProducer bulkProducer,  ObjectMapper objectMapper) {
        this.batchRepository = batchRepository;
        this.transactionRepository = transactionRepository;
        this.batchProducer = batchProducer;
        this.bulkProducer = bulkProducer;
        this.objectMapper = objectMapper;
    }


    public List<TransactionEvent> saveBulkTransactions(BulkEvent bulkEvent) throws Exception {
        List<TransactionEvent> events = new ArrayList<>();
        List<SingleTransaction> transactionsListToSave = new ArrayList<>();

        // Generate a unique bulkId for this operation
        String bulkId = bulkEvent.getBulkId();

        //Map each transactionEvent to a transactionEntity to prepare to save to db
        for (TransactionEvent transaction : bulkEvent.getTransactions()) {
            SingleTransaction transactionToSave = mapToTransactionEntity(transaction);
            transactionToSave.setBulkId(bulkId); //add bulkId to each transaction
            transactionsListToSave.add(transactionToSave);
        }

        //Save all transactions in bulk to database
        List<SingleTransaction> savedTransactions = transactionRepository.saveAll(transactionsListToSave);

        //Map saved entities back to DTOs for Kafka
        for (SingleTransaction savedTransaction : savedTransactions) {
            TransactionEvent event = mapToTransactionEvent(savedTransaction);
            event.setBulkId(bulkId); //include bulkId in the event
            String transactionJson = objectMapper.writeValueAsString(event);

            //Publish each transaction to Kafka
            bulkProducer.sendBulk(event.getUserId(), transactionJson);

            events.add(event);
        }

        return events;
    }

    public BatchEvent saveBatchTransactions(BatchEvent batchEvent) throws Exception {
        //Map DTO to BatchTransaction Entity
        BatchTransaction batchTransaction = mapToBatchEntity(batchEvent);

        List<SingleTransaction> transactionsToSave = new ArrayList<>();

        // Map and associate each TransactionEvent to BatchTransaction
        for (TransactionEvent transactionEvent : batchEvent.getTransactions()) {
            SingleTransaction transaction = mapToTransactionEntity(transactionEvent);
            transaction.setBatchId(batchEvent.getBatchId()); // Link to batch
            transactionsToSave.add(transaction);
        }

        //add transactions to batch entity
        batchTransaction.setTransactions(transactionsToSave);

        //Save the BatchTransaction entity (this saves transactions too due to CascadeType.ALL)
        BatchTransaction savedBatch =  batchRepository.save(batchTransaction);

        // Map saved BatchTransaction to DTO
        BatchEvent savedBatchEvent = mapToBatchEvent(savedBatch);
        String savedBatchEventJson = objectMapper.writeValueAsString(savedBatchEvent);

        // Publish the batch for fraud scoring
        batchProducer.sendBatch(savedBatchEvent.getBatchId(),savedBatchEventJson);

        return savedBatchEvent;
    }


    private SingleTransaction mapToTransactionEntity(TransactionEvent transactionEvent) {
        SingleTransaction transaction = new SingleTransaction();
        transaction.setTransactionId(transactionEvent.getTransactionId());
        transaction.setUserId(transactionEvent.getUserId());
        transaction.setPaymentType(transactionEvent.getPaymentType());
        transaction.setAmount(transactionEvent.getAmount());
        transaction.setCurrency(transactionEvent.getCurrency());
        transaction.setStatus(transactionEvent.getStatus());
        return transaction;
    }

    private TransactionEvent mapToTransactionEvent(SingleTransaction transaction) {
        TransactionEvent dto = new TransactionEvent();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setUserId(transaction.getUserId());
        dto.setPaymentType(transaction.getPaymentType());
        dto.setAmount(transaction.getAmount());
        dto.setCurrency(transaction.getCurrency());
        dto.setStatus(transaction.getStatus());
        return dto;
    }

    private BatchTransaction mapToBatchEntity(BatchEvent batchEvent) {
        BatchTransaction batchTransaction = new BatchTransaction();
        batchTransaction.setBatchId(batchEvent.getBatchId());
        batchTransaction.setBatchType(batchEvent.getBatchType());
        return batchTransaction;
    }

    private BatchEvent mapToBatchEvent(BatchTransaction batchTransaction) {
        BatchEvent batchEvent = new BatchEvent();
        batchEvent.setBatchId(batchTransaction.getBatchId());
        batchEvent.setBatchType(batchEvent.getBatchType());
        return batchEvent;
    }
}
