package com.realtimefraudmonitoring.transactionservice.dto;

import com.realtimefraudmonitoring.transactionservice.model.TransactionStatus;
import lombok.Data;

//import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class TransactionEvent {
    @NotNull(message = "Transaction ID cannot be null")
    private String transactionId; // Unique ID for the transaction

    @NotNull(message = "User ID cannot be null")
    private String userId; // ID of the user initiating the transaction

    @NotNull(message = "Payment type cannot be null")
    @Pattern(regexp = "CREDIT|DEBIT", message = "Payment type must be CREDIT or DEBIT")
    private String paymentType; // e.g., "CREDIT", "DEBIT"

    @NotNull(message = "Amount cannot be null")
//    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private double amount;

    @NotNull(message = "Currency cannot be null")
    private String currency;

    @NotNull(message = "Status cannot be null")
    private TransactionStatus status; // e.g., "PENDING", "COMPLETED", "FAILED", "SUSPICIOUS"

    // Optional metadata not sure whether to include
    private String bulkId; // Used for bulk transactions
    private String batchId; // Used for batch transactions
}