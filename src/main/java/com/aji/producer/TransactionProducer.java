package com.aji.producer;

import com.aji.service.BankAccountServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TransactionProducer {

    @Autowired
    private BankAccountServiceImpl bankAccountService;

    private ScheduledExecutorService scheduledExecutorService;

    @Value("${transaction.scheduler.pool-size: 2}")
    private int corePoolSize;

    @Value("${transaction.credit.transaction-no: 25}")
    private int creditNoOfTransaction;
    @Value("${transaction.credit.initial-delay: 5}")
    private int creditInitialDelay;
    @Value("${transaction.credit.period: 1}")
    private int creditPeriod;

    @Value("${transaction.debit.transaction-no: 25}")
    private int debitNoOfTransaction;
    @Value("${transaction.debit.initial-delay: 5}")
    private int debitInitialDelay;
    @Value("${transaction.debit.period: 1}")
    private int debitPeriod;


    @PostConstruct
    public void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);

        CreditTransaction creditTransaction = new CreditTransaction(creditNoOfTransaction, bankAccountService);
        scheduledExecutorService.scheduleAtFixedRate(creditTransaction, creditInitialDelay, creditPeriod, TimeUnit.SECONDS);

        DebitTransaction debitTransaction = new DebitTransaction(debitNoOfTransaction, bankAccountService);
        scheduledExecutorService.scheduleAtFixedRate(debitTransaction, debitInitialDelay, debitPeriod, TimeUnit.SECONDS);
    }


    @PreDestroy
    public void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            if (!scheduledExecutorService.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}