package com.realtimefraudmonitoring.batchprocessorservice.dto;

import lombok.Data;

@Data
public class TransactionEvent {
    private String transactionId; // Unique ID for the transaction
    private String userId; // ID of the user initiating the transaction
    private String paymentType; // e.g., "CREDIT", "DEBIT"
    private double amount;
    private String currency;
    private String status; // e.g., "PENDING", "COMPLETED"
    // Optional metadata not sure whether to include
    private String bulkId; // Used for bulk transactions
    private String batchId; // Used for batch transactions
}