package com.aji.service;

import com.aji.config.SharedCounter;
import com.aji.domain.Account;
import com.aji.domain.Transaction;
import com.aji.exception.AccountNotFoundException;
import com.aji.exception.InsufficientFundsException;
import com.aji.exception.InvalidTransactionType;
import com.aji.repository.AccountRepo;
import com.aji.repository.TransactionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;

    @Autowired
    public BankAccountServiceImpl(AccountRepo accountRepo, TransactionRepo transactionRepo) {
        this.accountRepo = accountRepo;
        this.transactionRepo = transactionRepo;
    }


    @Override
    @Transactional
    public void processTransaction(Transaction transaction) {

        switch (transaction.getTransactionType()) {
            case DEBIT -> processDebit(transaction);
            case CREDIT -> processCredit(transaction);
            default -> throw new InvalidTransactionType("Invalid transaction type");
        }

    }


    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW, isolation = SERIALIZABLE)
    public void processDebit(Transaction transaction) {
        Optional<Account> account = accountRepo.findById(transaction.getAccount().getId());

        if (account.isPresent()) {
            double currentBalance = account.get().getBalance();
            if (currentBalance > 0) {
                if (Math.signum(currentBalance - transaction.getAmount()) >= 0) {
                    account.get().setBalance((BigDecimal.valueOf(account.get().getBalance()).subtract(BigDecimal.valueOf(transaction.getAmount()))).doubleValue());
                    account.get().setLastModifiedDate(LocalDateTime.now());
                    transaction.setAccount(account.get());
                    transaction.setTransactionId(UUID.randomUUID());
                    try {
                        transactionRepo.save(transaction);
                        SharedCounter.getCounter().getAndIncrement();
                    } catch (RuntimeException e) {
                        SharedCounter.getCounter().getAndDecrement();
                        throw e;
                    }
                } else {
                    log.error("Insufficient funds");
                    throw new InsufficientFundsException("Insufficient funds");
                }
            } else {
                log.error("Insufficient funds");
                throw new InsufficientFundsException("Insufficient funds");
            }
        } else {
            log.error("Account not found");
            throw new AccountNotFoundException("Account not found");
        }
    }

    @Transactional(rollbackFor = {Exception.class, RuntimeException.class}, propagation = Propagation.REQUIRES_NEW, isolation = SERIALIZABLE)
    public void processCredit(Transaction transaction) {
        Optional<Account> account = accountRepo.findById(transaction.getAccount().getId());

        if (account.isPresent()) {
            account.get().setBalance((BigDecimal.valueOf(account.get().getBalance()).add(BigDecimal.valueOf(transaction.getAmount()))).doubleValue());
            account.get().setLastModifiedDate(LocalDateTime.now());
            transaction.setAccount(account.get());
            transaction.setTransactionId(UUID.randomUUID());
            try {
                transactionRepo.save(transaction);
                SharedCounter.getCounter().getAndIncrement();
            } catch (RuntimeException e) {
                SharedCounter.getCounter().getAndDecrement();
                throw e;
            }
        } else {
            log.error("Account not found");
            throw new AccountNotFoundException("Account not found");
        }
    }

    @Override
    public double retrieveBalance() {
        Optional<Account> account = accountRepo.findById(1L);
        if (account.isPresent()) {
            return account.get().getBalance();
        } else {
            log.error("Account not found");
            throw new AccountNotFoundException("Account not found");
        }
    }
}
