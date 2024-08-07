package com.aji.repository;


import com.aji.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE t.audited = ?1 ORDER BY t.id")
    List<Transaction> findAllUnAudited(int audited);

    @Modifying
    @Query("UPDATE Transaction t SET t.audited =:newValue WHERE t.id IN :ids")
    int updateAllById(List<Long> ids, int newValue);
}
