package com.realtimefraudmonitoring.batchprocessorservice.repository;

import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<SingleTransaction, Long> {
}
