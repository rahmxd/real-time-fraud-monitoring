package com.realtimefraudmonitoring.transactionservice.controller;


import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.model.Transaction;
import com.realtimefraudmonitoring.transactionservice.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionEvent transactionEvent) {
        try {
            Transaction savedTransaction = transactionService.saveTransaction(transactionEvent);
            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
