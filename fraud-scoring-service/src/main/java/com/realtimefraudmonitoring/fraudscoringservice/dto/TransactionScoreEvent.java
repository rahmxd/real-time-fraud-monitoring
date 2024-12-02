package com.realtimefraudmonitoring.fraudscoringservice.dto;

import lombok.Data;

@Data
public class TransactionScoreEvent {
    private String userId;
    private String transactionType; // e.g., "DEBIT", "CREDIT"
    private double amount;
    private String currency;
    private String status;
    private Double fraudScore;
}
