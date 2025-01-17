package com.realtimefraudmonitoring.batchprocessorservice.controller;

import com.realtimefraudmonitoring.avro.BatchEvent;
import com.realtimefraudmonitoring.avro.BulkEvent;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BatchEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.dto.BulkEventDTO;
import com.realtimefraudmonitoring.batchprocessorservice.service.BatchBulkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class BatchBulkController {

    private final BatchBulkService batchBulkService;

    public BatchBulkController(BatchBulkService batchBulkService) {
        this.batchBulkService = batchBulkService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkEventDTO> createBulkTransactions(@Valid @RequestBody BulkEventDTO bulkEventDTO) {
        try {
            BulkEventDTO savedBulk = batchBulkService.saveBulkTransactions(bulkEventDTO);
            return ResponseEntity.ok(savedBulk);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchEventDTO> createBatchTransactions(@Valid @RequestBody BatchEventDTO batchEventDTO) {
        try {
            BatchEventDTO savedBatch = batchBulkService.saveBatchTransactions(batchEventDTO);
            return ResponseEntity.ok(savedBatch);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
