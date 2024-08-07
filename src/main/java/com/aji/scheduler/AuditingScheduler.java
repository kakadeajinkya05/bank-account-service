package com.aji.scheduler;

import com.aji.config.SharedCounter;
import com.aji.domain.Transaction;
import com.aji.repository.TransactionRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class AuditingScheduler {

    public static final int MAX_SUM = 1_000_000;
    private final AtomicInteger count = new AtomicInteger();

    @Value("${auditing.max-transaction: 1000}")
    private int maxTransaction;

    @Autowired
    private TransactionRepo transactionRepo;

    private static BigDecimal calculateTotalSum(List<Transaction> TransactionContent, BigDecimal tatalSum, double maxSum, List<Long> list) {
        for (Transaction transaction : TransactionContent) {
            if ((tatalSum.doubleValue() + transaction.getAmount()) <= maxSum) {
                tatalSum = tatalSum.add(BigDecimal.valueOf(transaction.getAmount()));
                list.add(transaction.getId());
            } else {
                break;
            }
        }
        return tatalSum;
    }

    @Scheduled(cron = "${auditing.cron-expression}")
    @Transactional(rollbackOn = {Exception.class, RuntimeException.class})
    public void auditingScheduler() {
        if (SharedCounter.getCounter().get() >= maxTransaction) {

            List<Long> list = new ArrayList<>();
            BigDecimal totalSum = new BigDecimal("0.0");
            List<Transaction> transactionList = transactionRepo.findAllUnAudited(0);

            if (transactionList.size() > 0) {
                try {
                    totalSum = calculateTotalSum(transactionList, totalSum, MAX_SUM, list);
                    updateAndPrintAllTransactions(list, totalSum);
                } catch (RuntimeException e) {
                    log.error("Error while updating Audit Data {}", e.getMessage());
                }
            }

        }
    }

    private void updateAndPrintAllTransactions(List<Long> list, BigDecimal totalSum) {
        if (list.size() > 0) {
            int updatedTransactions = transactionRepo.updateAllById(list, 1);
            if (updatedTransactions > 0) {
                int counter = SharedCounter.getCounter().get();
                SharedCounter.getCounter().getAndSet((counter - maxTransaction));
                log.info("Before: {} After: {}", counter, SharedCounter.getCounter().get());
                int i = count.addAndGet(1);
                log.info("\nBatch Number: {} \n\t Total value: £{}\n\t Count of transactions: {}", i, totalSum, updatedTransactions);
            }
        }
    }
}
