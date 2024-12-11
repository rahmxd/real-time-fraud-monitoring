package com.realtimefraudmonitoring.batchprocessorservice.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class SingleTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String paymentType;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column
    private String bulkId; //to group bulk transactions

    @Column
    private String batchId;

}
