package com.realtimefraudmonitoring.batchprocessorservice.repository;

import com.realtimefraudmonitoring.batchprocessorservice.model.BatchTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<BatchTransaction, Long> {
}
