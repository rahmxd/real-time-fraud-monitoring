package com.realtimefraudmonitoring.transactionservice.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String paymentType;

    @Column(nullable = false, precision = 19, scale = 4) // Precision for BigDecimal
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING) // ensures the enum value is stored as a string in the database
    @Column(nullable = false)
    private TransactionStatus status;

    @Column
    private String bulkId; //to group bulk transactions

    @Column
    private String batchId;

    @Column(nullable = false)
    private boolean acknowledged = false;

}