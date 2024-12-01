package com.realtimefraudmonitoring.transactionservice.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String transactionId;
    private String userId;
    private double amount;
    private String transactionType; // e.g., "DEBIT", "CREDIT"
    private String timestamp;
}