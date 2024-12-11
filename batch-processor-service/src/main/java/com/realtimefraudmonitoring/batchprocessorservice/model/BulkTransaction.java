package com.realtimefraudmonitoring.batchprocessorservice.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BulkTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bulkId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<SingleTransaction> transactions;
}
