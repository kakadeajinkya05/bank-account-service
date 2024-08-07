package com.aji.controller;

import com.aji.service.BankAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private BankAccountService bankAccountService;

    @Test
    void shouldReturnBalance() {

        when(bankAccountService.retrieveBalance()).thenReturn(1000.00);

        ResponseEntity balance = accountController.getBalance();

        assertEquals(HttpStatus.OK, balance.getStatusCode());
        assertNotNull(balance.getBody());
    }
}
