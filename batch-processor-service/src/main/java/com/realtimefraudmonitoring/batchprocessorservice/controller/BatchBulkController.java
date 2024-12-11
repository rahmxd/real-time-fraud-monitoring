package com.realtimefraudmonitoring.batchprocessorservice.controller;

import com.realtimefraudmonitoring.batchprocessorservice.dto.BatchEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BulkEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.batchprocessorservice.service.BatchBulkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BatchBulkController {


    private final BatchBulkService batchBulkService;

    public BatchBulkController(BatchBulkService batchBulkService) {
        this.batchBulkService = batchBulkService;
    }

    // Endpoint for bulk transactions
    @PostMapping("/bulk")
    public ResponseEntity<List<TransactionEvent>> createBulkTransactions(@RequestBody BulkEvent bulkEvent) {
        try {
            List<TransactionEvent> savedTransactions = batchBulkService.saveBulkTransactions(bulkEvent);
            return ResponseEntity.ok(savedTransactions);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Endpoint for batch transactions
    @PostMapping("/batch")
    public ResponseEntity<BatchEvent> createBatchTransactions(@RequestBody BatchEvent batchTransaction) {
        try {
            BatchEvent savedBatch = batchBulkService.saveBatchTransactions(batchTransaction);
            return ResponseEntity.ok(savedBatch);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
