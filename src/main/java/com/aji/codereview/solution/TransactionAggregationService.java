package com.aji.codereview.solution;

import com.aji.exception.InvalidAccountNumber;
import com.aji.exception.InvalidTransactionAmount;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

public class TransactionAggregationService {

    private static final String PATTERN = "^ACC{3}\\d{5}$";

    private final ConcurrentMap<String, AtomicReference<Float>> map = new ConcurrentHashMap<>();

    public void processTransaction(String accountNumber, Float transactionAmount) throws RuntimeException {

        if (null == accountNumber && !accountNumber.matches(PATTERN)) {
            throw new InvalidAccountNumber("Account Number Invalid");
        }
        if (null == transactionAmount || transactionAmount <= 0) {
            throw new InvalidTransactionAmount("Transaction amount Invalid");
        }

        map.compute(accountNumber, (key, currentBalance) -> {
            if (currentBalance == null) {
                return new AtomicReference<>(transactionAmount);
            } else {
                Float newBalance = currentBalance.get() + transactionAmount;
                currentBalance.set(newBalance);
                return currentBalance;
            }
        });

    }

    public Float getBalance(String account) {
        AtomicReference<Float> balanceRef = map.get(account);
        if (balanceRef != null) {
            return balanceRef.get();
        } else {
            return -1F;
        }
    }
}
