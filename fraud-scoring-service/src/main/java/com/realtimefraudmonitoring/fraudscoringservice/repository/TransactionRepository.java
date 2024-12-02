package com.realtimefraudmonitoring.fraudscoringservice.repository;

import com.realtimefraudmonitoring.fraudscoringservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
