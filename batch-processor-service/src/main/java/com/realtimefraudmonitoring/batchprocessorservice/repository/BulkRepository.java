package com.realtimefraudmonitoring.batchprocessorservice.repository;

import com.realtimefraudmonitoring.batchprocessorservice.model.BulkEntity;
import com.realtimefraudmonitoring.batchprocessorservice.model.SingleTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BulkRepository extends JpaRepository<BulkEntity, Long> {


    BulkEntity findByBulkId(String bulkId);
    @Query("SELECT COUNT(t) FROM SingleTransactionEntity t WHERE t.bulk.id = :bulkId AND t.acknowledged = false")
    int countUnacknowledgedTransactions(@Param("bulkId") String bulkId);

    @Query("SELECT t FROM SingleTransactionEntity t WHERE t.transactionId = :transactionId")
    SingleTransactionEntity findTransactionById(@Param("transactionId") String transactionId);

    @Query("SELECT t FROM SingleTransactionEntity t WHERE t.acknowledged = false")
    List<SingleTransactionEntity> findUnacknowledgedTransactions();

    @Transactional
    @Modifying
    @Query("UPDATE SingleTransactionEntity t SET t.acknowledged = true WHERE t.transactionId = :transactionId")
    void saveTransaction(@Param("transactionId") String transactionId);
}

