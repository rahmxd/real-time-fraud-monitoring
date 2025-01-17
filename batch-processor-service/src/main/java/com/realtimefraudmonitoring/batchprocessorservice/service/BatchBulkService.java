package com.realtimefraudmonitoring.batchprocessorservice.service;

import com.realtimefraudmonitoring.avro.BatchEvent;
import com.realtimefraudmonitoring.avro.BulkEvent;
import com.realtimefraudmonitoring.avro.TransactionEvent;
import com.realtimefraudmonitoring.avro.TransactionStatus;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BatchEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BulkEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.dto.TransactionEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.kafka.consumer.BulkAcknowledgmentConsumer;
import com.realtimefraudmonitoring.batchprocessorservice.kafka.producer.BatchProducer;
import com.realtimefraudmonitoring.batchprocessorservice.kafka.producer.BulkProducer;
import com.realtimefraudmonitoring.batchprocessorservice.model.BatchEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.BulkEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransactionEntity;
import com.realtimefraudmonitoring.batchprocessorservice.repository.BatchRepository;
import com.realtimefraudmonitoring.batchprocessorservice.repository.BulkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.realtimefraudmonitoring.batchprocessorservice.util.MappingUtil.*;

@Service
public class BatchBulkService {

    private static final Logger logger = LoggerFactory.getLogger(BatchBulkService.class);
    private final BatchRepository batchRepository;
    private final BulkRepository bulkRepository;
    private final BatchProducer batchProducer;
    private final BulkProducer bulkProducer;

    public BatchBulkService(BatchRepository batchRepository, BulkRepository bulkRepository,
                            BatchProducer batchProducer, BulkProducer bulkProducer) {
        this.batchRepository = batchRepository;
        this.bulkRepository = bulkRepository;
        this.batchProducer = batchProducer;
        this.bulkProducer = bulkProducer;
    }


    @Transactional
    @Async("batchProcessorExecutor")
    public BatchEventDTO saveBatchTransactions(BatchEventDTO batchEventDTO) {
        BatchEntity batchEntity = new BatchEntity();
        batchEntity.setBatchId(batchEventDTO.getBatchId());
        batchEntity.setBatchType(batchEventDTO.getBatchType());
        batchEntity.setTransactions(new ArrayList<>());

        List<TransactionEvent> allTransactions = new ArrayList<>();

        // Split batch into chunks
        int chunkSize = 25; // Define split size
        List<List<TransactionEventDTO>> chunks = splitBatch(batchEventDTO.getTransactions(), chunkSize);

        // Process each chunk async
        for (List<TransactionEventDTO> chunk : chunks) {
            String splitId = UUID.randomUUID().toString(); // Assign splitId for each chunk
            List<TransactionEvent> splitTransactions = new ArrayList<>();

            for (TransactionEventDTO transactionDTO : chunk) {
                transactionDTO.validateBulkOrBatch();
                SingleTransactionEntity transaction = mapToTransactionEntity(transactionDTO, batchEntity, splitId);
                transaction.setBatchId(batchEventDTO.getBatchId());
                transaction.setSplitId(splitId);
                batchEntity.getTransactions().add(transaction);

                TransactionEvent avroTransaction = mapToAvro(transaction);
                avroTransaction.setSplitId(splitId);
                splitTransactions.add(avroTransaction);
                allTransactions.add(avroTransaction);
            }

            // Publish split to Kafka
            BatchEvent splitEvent = BatchEvent.newBuilder()
                    .setBatchId(batchEventDTO.getBatchId())
                    .setSplitId(splitId)
                    .setTransactions(splitTransactions)
                    .setStatus(TransactionStatus.PENDING)
                    .build();

            batchProducer.sendBatch(splitEvent);
        }

        // Save batch and its transactions
        BatchEntity savedBatch = batchRepository.save(batchEntity);
        return mapToBatchDTO(savedBatch);
    }

    @Transactional
    @Async("batchProcessorExecutor")
    public BulkEventDTO saveBulkTransactions(BulkEventDTO bulkEventDTO) {
        BulkEntity bulkEntity = new BulkEntity();
        bulkEntity.setBulkId(bulkEventDTO.getBulkId());
        bulkEntity.setTransactions(new ArrayList<>());

        // Save transactions and publish each one to Kafka
        for (TransactionEventDTO transactionDTO : bulkEventDTO.getTransactions()) {
            transactionDTO.validateBulkOrBatch();

            SingleTransactionEntity transaction = mapToTransactionEntity(transactionDTO, bulkEntity);
            transaction.setAcknowledged(false); // Default to false
            bulkEntity.getTransactions().add(transaction);

            // Publish transaction to Kafka
            TransactionEvent avroTransaction = mapToAvro(transaction);
            bulkProducer.sendTransaction(avroTransaction);
        }

        // Save BulkEntity (cascade transactions)
        BulkEntity savedBulk = bulkRepository.save(bulkEntity);
        return mapToBulkDTO(savedBulk);
    }

    @Transactional
    public void handleBulkAcknowledgment(String transactionId) {

        SingleTransactionEntity transaction = bulkRepository.findTransactionById(transactionId);

        if (transaction == null || transaction.getBulkId() == null) {
            logger.warn("Transaction with ID {} does not have a valid Bulk ID.", transactionId);
            return;
        }

        if (!transaction.isAcknowledged()) {
            bulkRepository.saveTransaction(transaction.getTransactionId());
        }

        // Check if all transactions in the bulk are acknowledged
        BulkEntity bulk = bulkRepository.findByBulkId(transaction.getBulkId());

        if (bulk == null) {
            logger.warn("No Bulk found for transaction ID {}.", transactionId);
            return;
        }

        if (!bulk.isAcknowledged()) {
            int unacknowledgedCount = bulkRepository.countUnacknowledgedTransactions(bulk.getBulkId());
            if (unacknowledgedCount == 0) {
                bulk.setAcknowledged(true);
                bulkRepository.save(bulk);
                logger.info("Bulk with ID {} is now fully acknowledged.", bulk.getBulkId());
            }
        }
    }

    public void handleBatchAcknowledgment(BatchEvent event) {
        // Fetch the batch entity from the database
        BatchEntity batch = batchRepository.findByBatchId(event.getBatchId().toString());
        if (batch == null) {
            logger.warn("Batch with ID {} not found in the database", event.getBatchId());
            return;
        }

        // Idempotency Check
        if (batch.isAcknowledged()) {
            logger.info("Acknowledgment for batch ID {} already processed. Skipping.", event.getBatchId());
            return;
        }

        // Update the batch status
        batch.setAcknowledged(true);
        batchRepository.save(batch);
        logger.info("Batch {} acknowledged and updated in the database", event.getBatchId());
    }

//    @Scheduled(fixedRate = 30000) // Retry every 30 seconds
//    public void retryUnacknowledgedTransactions() {
//        try {
//            List<SingleTransactionEntity> unacknowledgedTransactions = bulkRepository.findUnacknowledgedTransactions();
//            for (SingleTransactionEntity transaction : unacknowledgedTransactions) {
//                logger.info("Retrying unacknowledged transaction: {}", transaction.getTransactionId());
//                TransactionEvent avroTransaction = mapToAvro(transaction);
//                bulkProducer.sendTransaction(avroTransaction);
//            }
//        } catch (Exception e) {
//            logger.error("Error occurred during unacknowledged transaction retry: {}", e.getMessage(), e);
//        }
//    }
    private List<List<TransactionEventDTO>> splitBatch(List<TransactionEventDTO> transactions, int chunkSize) {
        List<List<TransactionEventDTO>> chunks = new ArrayList<>();
        for (int i = 0; i < transactions.size(); i += chunkSize) {
            chunks.add(transactions.subList(i, Math.min(i + chunkSize, transactions.size())));
        }
        return chunks;
    }

}


