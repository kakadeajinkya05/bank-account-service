package com.aji.service;

import com.aji.domain.Transaction;

public interface BankAccountService {
    
    void processTransaction(Transaction transaction);

    double retrieveBalance();
}
