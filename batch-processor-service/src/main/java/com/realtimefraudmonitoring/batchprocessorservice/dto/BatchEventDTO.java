package com.realtimefraudmonitoring.batchprocessorservice.dto;

import com.realtimefraudmonitoring.batchprocessorservice.model.TransactionStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BatchEventDTO {
    @NotBlank(message = "Batch ID cannot be blank")
    private String batchId; //Unique ID for the batch

    @NotBlank(message = "Batch Type cannot be blank")
    private String batchType; //e.g. "PAYROLL", "DISBURSEMENT"

    //needs validation too
    private List<TransactionEventDTO> transactions; //List of transactions in the batch
    //needs to go as only useful and relevant in List<transactionsEventDTO> transactions field

    @NotNull(message = "Status cannot be null")
    private TransactionStatus status;

    private boolean acknowledged = false;
}
