package com.aji.producer;

import com.aji.domain.Account;
import com.aji.domain.Transaction;
import com.aji.domain.TransactionType;
import com.aji.service.BankAccountService;
import com.aji.service.BankAccountServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class DebitTransaction implements Runnable {

    public static final int NEW_SCALE = 2;
    private final int noOfTransaction;
    private final BankAccountService bankAccountService;

    public static final int LOWER_BOUND = 20_000;
    public static final int UPPER_BOUND = 500_000;

    public DebitTransaction(int noOfTransaction, BankAccountServiceImpl bankAccountService) {
        this.noOfTransaction = noOfTransaction;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public void run() {

        for (int i = 0; i < noOfTransaction; i++) {
            double randomCredit = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(LOWER_BOUND, UPPER_BOUND)).setScale(NEW_SCALE, RoundingMode.HALF_UP).doubleValue();
            log.info("debit_random_value={}", randomCredit);
            Account account = Account.builder().id(1L).build();
            Transaction creditFor = Transaction.builder().account(account).transactionType(TransactionType.DEBIT).amount(randomCredit).audited(0).createdDate(LocalDateTime.now()).build();
            try {
                bankAccountService.processTransaction(creditFor);
            } catch (RuntimeException e) {
                log.error("Error while executing credit transaction: {} ", e.getMessage());
            }
        }

    }
}
