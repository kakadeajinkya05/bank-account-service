package com.aji.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidTransactionAmount extends RuntimeException {
    public InvalidTransactionAmount(String message) {
        super(message);
    }
}