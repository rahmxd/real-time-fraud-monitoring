package com.realtimefraudmonitoring.batchprocessorservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkEvent {
    private String bulkId; //Unique ID for the bulk transaction set
    private List<TransactionEvent> transactions; //List of single transactions
}
