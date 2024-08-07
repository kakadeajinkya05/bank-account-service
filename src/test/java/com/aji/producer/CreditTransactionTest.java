package com.aji.producer;

import com.aji.domain.Transaction;
import com.aji.service.BankAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CreditTransactionTest {

    @Mock
    private BankAccountServiceImpl bankAccountService;
    @InjectMocks
    private CreditTransaction creditTransaction = new CreditTransaction(2, bankAccountService);

    @Test
    void shouldTestTransactionProducer() {
        bankAccountService = mock(BankAccountServiceImpl.class);

        doNothing().when(bankAccountService).processTransaction(any(Transaction.class));

        creditTransaction.run();
    }

    @Test
    void shouldThrowAnExceptionForNullBankAccountService() {

        doThrow(NullPointerException.class).when(bankAccountService).processCredit(any(Transaction.class));

        creditTransaction.run();
    }
}
