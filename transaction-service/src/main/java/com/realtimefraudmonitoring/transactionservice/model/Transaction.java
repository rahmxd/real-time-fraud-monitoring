package com.realtimefraudmonitoring.transactionservice.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String transactionType; // individual, bulk, batch

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status; // pending, completed, flagged

}