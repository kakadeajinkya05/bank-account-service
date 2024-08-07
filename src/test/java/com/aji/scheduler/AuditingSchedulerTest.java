package com.aji.scheduler;

import com.aji.config.SharedCounter;
import com.aji.domain.Account;
import com.aji.domain.Transaction;
import com.aji.repository.TransactionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AuditingSchedulerTest {

    @Captor
    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    @Captor
    ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
    @InjectMocks
    private AuditingScheduler auditingScheduler;
    @Mock
    private TransactionRepo transactionRepo;

    @Test
    void shouldAuditTransactionWhereTotalSumLessIsThanMaxSum() {
        SharedCounter.getCounter().getAndSet(1000);
        Transaction transaction = prepeareTransaction();
        List<Transaction> listResponse = List.of(transaction);

        when(transactionRepo.findAllUnAudited(anyInt())).thenReturn(listResponse);
        when(transactionRepo.updateAllById(anyList(), anyInt())).thenReturn(1);

        auditingScheduler.auditingScheduler();

        verify(transactionRepo, times(1)).findAllUnAudited(intCaptor.capture());
        verify(transactionRepo, times(1)).updateAllById(listCaptor.capture(), intCaptor.capture());

    }

    @Test
    void shouldAuditTransactionWhereTotalSumEqualsThanMaxSum() {
        SharedCounter.getCounter().getAndSet(1000);
        Transaction transaction = prepeareTransaction();
        transaction.setAmount(1_000_000);
        List<Transaction> listResponse = List.of(transaction);

        when(transactionRepo.findAllUnAudited(anyInt())).thenReturn(listResponse);
        when(transactionRepo.updateAllById(anyList(), anyInt())).thenReturn(1);

        auditingScheduler.auditingScheduler();

        verify(transactionRepo, times(1)).findAllUnAudited(intCaptor.capture());
        verify(transactionRepo, times(1)).updateAllById(listCaptor.capture(), intCaptor.capture());

    }

    @Test
    void shouldNotAuditTransactionWhereTotalSumGreaterThanMaxSum() {
        SharedCounter.getCounter().getAndSet(1000);
        Transaction transaction = prepeareTransaction();
        transaction.setAmount(1_000_001);
        List<Transaction> listResponse = List.of(transaction);

        when(transactionRepo.findAllUnAudited(anyInt())).thenReturn(listResponse);

        auditingScheduler.auditingScheduler();

        verify(transactionRepo, times(1)).findAllUnAudited(intCaptor.capture());
        verify(transactionRepo, times(0)).updateAllById(listCaptor.capture(), intCaptor.capture());

    }

    @Test
    void shouldNotAuditTransactionForAuditedOnes() {
        SharedCounter.getCounter().getAndSet(1000);
        Transaction transaction = prepeareTransaction();
        transaction.setAmount(1_000_001);
        List<Transaction> pagedResponse = mock(List.class);

        when(transactionRepo.findAllUnAudited(anyInt())).thenReturn(pagedResponse);

        auditingScheduler.auditingScheduler();

        verify(transactionRepo, times(1)).findAllUnAudited(intCaptor.capture());
        verify(transactionRepo, times(0)).updateAllById(listCaptor.capture(), intCaptor.capture());

    }

    private Account prepareAccount() {
        return Account.builder().id(1L).balance(100.00).build();
    }

    private Transaction prepeareTransaction() {
        return Transaction.builder().audited(0).amount(10.50).account(prepareAccount()).build();
    }
}