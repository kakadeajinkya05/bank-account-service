package com.aji.producer;

import com.aji.service.BankAccountServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
public class TransactionProducerTest {

    @InjectMocks
    private TransactionProducer transactionProducer;

    @Mock
    private BankAccountServiceImpl bankAccountService;

    @Test
    void shouldTestTransactionProducer() {
        ReflectionTestUtils.setField(transactionProducer, "corePoolSize", 2);
        ReflectionTestUtils.setField(transactionProducer, "creditNoOfTransaction", 25);
        ReflectionTestUtils.setField(transactionProducer, "creditInitialDelay", 5);
        ReflectionTestUtils.setField(transactionProducer, "creditPeriod", 1);
        ReflectionTestUtils.setField(transactionProducer, "debitNoOfTransaction", 25);
        ReflectionTestUtils.setField(transactionProducer, "debitInitialDelay", 5);
        ReflectionTestUtils.setField(transactionProducer, "debitPeriod", 1);



        transactionProducer.init();
        transactionProducer.shutdown();
    }
}
