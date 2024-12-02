package com.realtimefraudmonitoring.transactionservice.dto;

import lombok.Data;

@Data
public class TransactionEvent {
    private String transactionId; // Unique ID for the transaction
    private String userId; // ID of the user initiating the transaction
    private String paymentType; // e.g., "CREDIT", "DEBIT"
    private double amount;
    private String currency;
    private String status; // e.g., "PENDING", "COMPLETED"
}