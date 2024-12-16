package com.realtimefraudmonitoring.transactionservice.service;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.dto.TransactionEventDTO;
import com.realtimefraudmonitoring.transactionservice.kafka.TransactionProducer;
import com.realtimefraudmonitoring.transactionservice.model.Transaction;
import com.realtimefraudmonitoring.transactionservice.model.TransactionStatus;
import com.realtimefraudmonitoring.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;

    public TransactionService(TransactionRepository transactionRepository, TransactionProducer transactionProducer) {
        this.transactionRepository = transactionRepository;
        this.transactionProducer = transactionProducer;
    }

    public TransactionEventDTO saveTransaction(TransactionEventDTO transactionEventDTO) {
        // Map DTO to Entity and save to the database
        Transaction transactionEntity = mapToTransactionEntity(transactionEventDTO);
        transactionRepository.save(transactionEntity);

        // Map DTO to Avro and send to Kafka
        TransactionEvent avroEvent = mapToAvro(transactionEventDTO);
        transactionProducer.sendTransaction(avroEvent);

        return transactionEventDTO;
    }

    private Transaction mapToTransactionEntity(TransactionEventDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(dto.getTransactionId());
        transaction.setUserId(dto.getUserId());
        transaction.setPaymentType(dto.getPaymentType());
        transaction.setAmount(dto.getAmount());
        transaction.setCurrency(dto.getCurrency());
        transaction.setStatus(dto.getStatus());
        transaction.setBulkId(dto.getBulkId());
        transaction.setBatchId(dto.getBatchId());
        return transaction;
    }

    private TransactionEvent mapToAvro(TransactionEventDTO dto) {
        return TransactionEvent.newBuilder()
                .setTransactionId(dto.getTransactionId())
                .setUserId(dto.getUserId())
                .setPaymentType(com.realtimefraudmonitoring.avro.PaymentType.valueOf(dto.getPaymentType()))
                .setAmount(ByteBuffer.wrap(dto.getAmount().unscaledValue().toByteArray())) //converted to ByteBuffer
                .setCurrency(dto.getCurrency())
                .setStatus(mapToAvroStatus(dto.getStatus()))
                .setBulkId(dto.getBulkId())
                .setBatchId(dto.getBatchId())
                .build();
    }

    private com.realtimefraudmonitoring.avro.TransactionStatus mapToAvroStatus(TransactionStatus status) {
        switch (status) {
            case PENDING:
                return com.realtimefraudmonitoring.avro.TransactionStatus.PENDING;
            case COMPLETED:
                return com.realtimefraudmonitoring.avro.TransactionStatus.COMPLETED;
            case FAILED:
                return com.realtimefraudmonitoring.avro.TransactionStatus.FAILED;
            case SUSPICIOUS:
                return com.realtimefraudmonitoring.avro.TransactionStatus.SUSPICIOUS;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}
