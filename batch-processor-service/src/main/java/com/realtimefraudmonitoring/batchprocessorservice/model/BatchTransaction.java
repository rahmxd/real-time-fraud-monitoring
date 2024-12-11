package com.realtimefraudmonitoring.batchprocessorservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BatchTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String batchId; //Unique ID for the batch

    @Column(nullable = false)
    private String batchType; //e.g. "PAYROLL", "DISBURSEMENT"

    @Column(nullable = false)
    private String status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SingleTransaction> transactions;
}