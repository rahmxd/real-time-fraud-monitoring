package com.realtimefraudmonitoring.transactionservice.model;

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
    private String bulkId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
