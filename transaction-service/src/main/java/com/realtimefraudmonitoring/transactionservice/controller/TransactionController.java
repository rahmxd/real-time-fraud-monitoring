package com.realtimefraudmonitoring.transactionservice.controller;

import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
import com.realtimefraudmonitoring.transactionservice.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
@Validated
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionEvent> createTransaction(@Valid @RequestBody TransactionEvent transactionEvent) throws Exception {
        logger.info("Received transaction event: {}", transactionEvent);
        TransactionEvent savedTransaction = transactionService.saveTransaction(transactionEvent);
        return ResponseEntity.ok(savedTransaction);
    }
}


//package com.realtimefraudmonitoring.transactionservice.controller;
//
//
//import com.realtimefraudmonitoring.transactionservice.dto.TransactionEvent;
//import com.realtimefraudmonitoring.transactionservice.service.TransactionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//
//@RestController
//@RequestMapping("/api/transaction")
//@Validated
//public class TransactionController {
//
//    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
//    private final TransactionService transactionService;
//
//    public TransactionController(TransactionService transactionService) {
//        this.transactionService = transactionService;
//    }
//
//    @PostMapping
//    public ResponseEntity<TransactionEvent> createTransaction(@Valid @RequestBody TransactionEvent transactionEvent) {
//        try {
//            logger.info("Received transaction event: {}", transactionEvent);
//            TransactionEvent savedTransaction = transactionService.saveTransaction(transactionEvent);
//            return ResponseEntity.ok(savedTransaction);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).build();
//        }
//    }
//
//}
