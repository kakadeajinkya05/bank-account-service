package com.aji.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidTransactionType extends RuntimeException {
    public InvalidTransactionType(String message) {
        super(message);
    }
}