package com.realtimefraudmonitoring.batchprocessorservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BatchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String batchId; //Unique ID for the batch

    @Column(nullable = false)
    private String batchType; //e.g. "PAYROLL", "DISBURSEMENT"

    //needs to go
    @Column(nullable = false)
    private TransactionStatus status;

    @OneToMany(mappedBy= "batchEntity", cascade = CascadeType.ALL)
    private List<SingleTransactionEntity> transactions;

    @Column(nullable = false)
    private boolean acknowledged = false;

    @Column
    private String splitId;
}