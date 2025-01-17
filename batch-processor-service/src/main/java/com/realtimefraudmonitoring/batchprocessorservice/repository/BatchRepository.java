package com.realtimefraudmonitoring.batchprocessorservice.repository;

import com.realtimefraudmonitoring.batchprocessorservice.model.BatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<BatchEntity, Long> {
    BatchEntity findByBatchId(String batchId);
}
