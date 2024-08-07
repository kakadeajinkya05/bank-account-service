package com.aji.controller;

import com.aji.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @GetMapping(path = "/balance")
    public ResponseEntity getBalance() {
        return ResponseEntity.ok(bankAccountService.retrieveBalance());
    }
}
