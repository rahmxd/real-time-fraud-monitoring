package com.realtimefraudmonitoring.transactionservice.controller;


import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionEvent> createTransaction(@RequestBody TransactionEvent transactionEvent) {
        try {
            TransactionEvent savedTransaction = transactionService.saveTransaction(transactionEvent);
            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
