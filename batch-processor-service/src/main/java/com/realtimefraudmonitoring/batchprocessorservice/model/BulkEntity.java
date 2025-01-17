package com.realtimefraudmonitoring.batchprocessorservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BulkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bulkId;

    @OneToMany(mappedBy = "bulkEntity", cascade = CascadeType.ALL)
    private List<SingleTransactionEntity> transactions;

    @Column(nullable = false)
    private boolean acknowledged = false;
}
