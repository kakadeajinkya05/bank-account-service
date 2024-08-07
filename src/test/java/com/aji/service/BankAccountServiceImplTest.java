package com.aji.service;

import com.aji.domain.Account;
import com.aji.domain.Transaction;
import com.aji.exception.AccountNotFoundException;
import com.aji.exception.InsufficientFundsException;
import com.aji.exception.InvalidTransactionType;
import com.aji.repository.AccountRepo;
import com.aji.repository.TransactionRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.aji.domain.TransactionType.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BankAccountServiceImplTest {

    @Captor
    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
    @Captor
    ArgumentCaptor<Long> transactionIdCaptor = ArgumentCaptor.forClass(Long.class);
    @Mock
    private AccountRepo accountRepo;
    @Mock
    private TransactionRepo transactionRepo;
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @Test
    void shouldProcessCreditTransaction() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(CREDIT);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(transaction.getAccount()));
        when(transactionRepo.save(transaction)).thenReturn(transaction);

        bankAccountService.processTransaction(transaction);

        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
        verify(transactionRepo, times(1)).save(transactionCaptor.capture());
    }

    @Test
    void shouldNotProcessCreditTransactionForInvalidAccount() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(CREDIT);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(null));

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class,
                () -> bankAccountService.processTransaction(transaction), "Account not found");

        assertTrue(thrown.getMessage().contains("Account not found"));
        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
    }

    @Test
    void shouldThrowAnExceptionForInvalidTransactionType() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(NONE);

        InvalidTransactionType thrown = assertThrows(InvalidTransactionType.class,
                () -> bankAccountService.processTransaction(transaction), "Invalid transaction type");

        assertTrue(thrown.getMessage().contains("Invalid transaction type"));
    }

    @Test
    void shouldProcessDebitTransaction() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(DEBIT);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(transaction.getAccount()));
        when(transactionRepo.save(transaction)).thenReturn(transaction);

        bankAccountService.processTransaction(transaction);

        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
        verify(transactionRepo, times(1)).save(transactionCaptor.capture());
    }

    @Test
    void shouldNotProcessDebitTransactionForInvalidAccount() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(DEBIT);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(null));

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class,
                () -> bankAccountService.processTransaction(transaction), "Account not found");

        assertTrue(thrown.getMessage().contains("Account not found"));
        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
    }

    @Test
    void shouldNotProcessDebitTransactionForZeroBalance() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(DEBIT);
        transaction.getAccount().setBalance(0.00);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(transaction.getAccount()));

        InsufficientFundsException thrown = assertThrows(InsufficientFundsException.class,
                () -> bankAccountService.processTransaction(transaction), "Insufficient funds");

        assertTrue(thrown.getMessage().contains("Insufficient funds"));
        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
        verify(transactionRepo, times(0)).save(transactionCaptor.capture());
    }

    @Test
    void shouldNotProcessDebitTransactionForNegativeBalanceValue() {
        Transaction transaction = prepeareTransaction();
        transaction.setTransactionType(DEBIT);
        transaction.setAmount(101);

        when(accountRepo.findById(transaction.getAccount().getId())).thenReturn(Optional.ofNullable(transaction.getAccount()));

        InsufficientFundsException thrown = assertThrows(InsufficientFundsException.class,
                () -> bankAccountService.processTransaction(transaction), "Insufficient funds");

        assertTrue(thrown.getMessage().contains("Insufficient funds"));
        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
        verify(transactionRepo, times(0)).save(transactionCaptor.capture());
    }

    @Test
    void shouldRetrieveBalance() {
        Account account = prepareAccount();

        when(accountRepo.findById(1L)).thenReturn(Optional.ofNullable(account));

        bankAccountService.retrieveBalance();

        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
    }

    @Test
    void shouldThrowAnExceptionIfAccountNotFound() {

        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class,
                () -> bankAccountService.retrieveBalance(), "Account not found");

        assertTrue(thrown.getMessage().contains("Account not found"));
        verify(accountRepo, times(1)).findById(transactionIdCaptor.capture());
    }

    private Account prepareAccount() {
        return Account.builder().id(1L).balance(100.00).build();
    }

    private Transaction prepeareTransaction() {
        return Transaction.builder().audited(0).amount(10.50).account(prepareAccount()).build();
    }

}
