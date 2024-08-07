package com.aji.producer;

import com.aji.domain.Transaction;
import com.aji.service.BankAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class DebitTransactionTest {

    @Mock
    private BankAccountServiceImpl bankAccountService;
    @InjectMocks
    private DebitTransaction debitTransaction = new DebitTransaction(2, bankAccountService);

    @Test
    void shouldTestTransactionProducer() {
        bankAccountService = mock(BankAccountServiceImpl.class);

        doNothing().when(bankAccountService).processTransaction(any(Transaction.class));

        debitTransaction.run();
    }

    @Test
    void shouldThrowAnExceptionForNullBankAccountService() {

        doThrow(NullPointerException.class).when(bankAccountService).processDebit(any(Transaction.class));

        debitTransaction.run();
    }
}
