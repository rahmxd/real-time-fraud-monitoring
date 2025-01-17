package com.realtimefraudmonitoring.batchprocessorservice.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BulkEventDTO {
    @NotBlank(message = "Bulk ID cannot be blank")
    private String bulkId; //Unique ID for the bulk transaction set

    //need to add validation
    private List<TransactionEventDTO> transactions; //List of single transactions

    private boolean acknowledged = false;
}
