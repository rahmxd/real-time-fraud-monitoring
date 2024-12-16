package com.realtimefraudmonitoring.transactionservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.realtimefraudmonitoring.transactionservice.config.StrictBigDecimalDeserializer;
import com.realtimefraudmonitoring.transactionservice.model.TransactionStatus;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
public class TransactionEventDTO {

    @NotBlank(message = "Transaction ID cannot be blank")
    private String transactionId;

    @NotBlank(message = "User ID cannot be blank")
    private String userId;

    @NotBlank(message = "Payment type cannot be blank")
    @Pattern(regexp = "CREDIT|DEBIT", message = "Payment type must be CREDIT or DEBIT")
    private String paymentType;

    @JsonDeserialize(using = StrictBigDecimalDeserializer.class)
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency cannot be blank")
    private String currency;

    @NotNull(message = "Status cannot be null")
    private TransactionStatus status;

    private String bulkId; // Optional
    private String batchId; // Optional

    //to call wherever dto is processed e.g. saveTransaction method
    public void validateBulkOrBatch() {
        if (bulkId != null && batchId != null) {
            throw new IllegalArgumentException("Transaction cannot belong to both a bulk and a batch.");
        }
    }
}
