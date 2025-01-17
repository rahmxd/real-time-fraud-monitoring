package com.realtimefraudmonitoring.batchprocessorservice.util;

import com.realtimefraudmonitoring.avro.TransactionEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BatchEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BulkEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.dto.TransactionEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.model.BatchEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.BulkEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransactionEntity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MappingUtil {

    public static BulkEventDTO mapToBulkDTO(BulkEntity savedBulk) {
        BulkEventDTO bulkDTO = new BulkEventDTO();
        bulkDTO.setBulkId(savedBulk.getBulkId());
        bulkDTO.setTransactions(mapToTransactionDTOList(savedBulk.getTransactions()));
        bulkDTO.setAcknowledged(savedBulk.isAcknowledged());
        return bulkDTO;
    }


    public static List<TransactionEventDTO> mapToTransactionDTOList(List<SingleTransactionEntity> transactions) {
        List<TransactionEventDTO> transactionDTOs = new ArrayList<>();
        for (SingleTransactionEntity transaction : transactions) {
            TransactionEventDTO dto = new TransactionEventDTO();
            dto.setTransactionId(transaction.getTransactionId());
            dto.setUserId(transaction.getUserId());
            dto.setPaymentType(transaction.getPaymentType());
            dto.setAmount(transaction.getAmount());
            dto.setCurrency(transaction.getCurrency());
            dto.setStatus(transaction.getStatus());
            dto.setBatchId(transaction.getBatchId());
            dto.setBulkId(transaction.getBulkId());
            dto.setAcknowledged(transaction.isAcknowledged());
            dto.setSplitId(transaction.getSplitId());
            dto.setSource(transaction.getSource());
            transactionDTOs.add(dto);
        }
        return transactionDTOs;
    }

    // For bulk transactions
    public static SingleTransactionEntity mapToTransactionEntity(TransactionEventDTO dto, BulkEntity bulkEntity) {
        SingleTransactionEntity entity = new SingleTransactionEntity();
        entity.setTransactionId(dto.getTransactionId());
        entity.setUserId(dto.getUserId());
        entity.setPaymentType(dto.getPaymentType());
        entity.setAmount(dto.getAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setStatus(dto.getStatus());
        entity.setAcknowledged(dto.isAcknowledged());
        entity.setSource(dto.getSource());
        entity.setBulkEntity(bulkEntity); // Set BulkEntity relationship
        entity.setBulkId(bulkEntity.getBulkId()); // Explicitly set bulkId
        return entity;
    }

    // For batch transactions
    public static SingleTransactionEntity mapToTransactionEntity(TransactionEventDTO dto, BatchEntity batchEntity, String splitId) {
        SingleTransactionEntity entity = new SingleTransactionEntity();
        entity.setTransactionId(dto.getTransactionId());
        entity.setUserId(dto.getUserId());
        entity.setPaymentType(dto.getPaymentType());
        entity.setAmount(dto.getAmount());
        entity.setCurrency(dto.getCurrency());
        entity.setStatus(dto.getStatus());
        entity.setAcknowledged(dto.isAcknowledged());
        entity.setSource(dto.getSource());
        entity.setBatchEntity(batchEntity); // Set BatchEntity relationship
        entity.setBatchId(batchEntity.getBatchId()); // Explicitly set batchId
        entity.setSplitId(splitId); // Only for batch transactions
        return entity;
    }



    public static TransactionEvent mapToAvro(SingleTransactionEntity entity) {
        return TransactionEvent.newBuilder()
                .setTransactionId(entity.getTransactionId())
                .setUserId(entity.getUserId())
                .setPaymentType(com.realtimefraudmonitoring.avro.PaymentType.valueOf(entity.getPaymentType()))
                .setAmount(ByteBuffer.wrap(entity.getAmount().unscaledValue().toByteArray())) // BigDecimal to ByteBuffer
                .setCurrency(entity.getCurrency())
                .setStatus(com.realtimefraudmonitoring.avro.TransactionStatus.valueOf(entity.getStatus().name()))
                .setSource("batch-processor-service")
                .build();
    }

    public static BatchEventDTO mapToBatchDTO(BatchEntity batchEntity) {
        BatchEventDTO batchEventDTO = new BatchEventDTO();
        batchEventDTO.setBatchId(batchEntity.getBatchId());
        batchEventDTO.setBatchType(batchEntity.getBatchType());
        batchEventDTO.setStatus(batchEntity.getStatus());
        batchEventDTO.setTransactions(mapToTransactionDTOList(batchEntity.getTransactions()));
        batchEventDTO.setAcknowledged(batchEntity.isAcknowledged());
        return batchEventDTO;
    }
}
