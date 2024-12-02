package com.realtimefraudmonitoring.transactionservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchEvent {
    private String batchId; //Unique ID for the batch
    private String batchType; //e.g. "PAYROLL", "DISBURSEMENT"
    private List<TransactionEvent> transactions; //List of transactions in the batch
}
