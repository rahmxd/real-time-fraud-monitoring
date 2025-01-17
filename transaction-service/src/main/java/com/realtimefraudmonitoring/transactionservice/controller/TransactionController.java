package com.realtimefraudmonitoring.transactionservice.controller;

import com.realtimefraudmonitoring.transactionservice.dto.TransactionEventDTO;
import com.realtimefraudmonitoring.transactionservice.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

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
    public CompletableFuture<ResponseEntity<TransactionEventDTO>> createTransaction(
            @Valid @RequestBody TransactionEventDTO transactionEventDTO
    ) throws Exception {
        logger.info("Received transaction event: {}", transactionEventDTO);
        return transactionService.saveTransactionAsync(transactionEventDTO)
                .thenApply(ResponseEntity::ok);
    }
}
