package com.realtimefraudmonitoring.transactionservice.dto;

import lombok.Data;

@Data
public class TransactionRequest {
    private String userId;
    private String transactionType; // e.g., "DEBIT", "CREDIT"
    private double amount;
    private String currency;
    private String status;
}