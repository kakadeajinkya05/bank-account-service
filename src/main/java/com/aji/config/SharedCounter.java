package com.aji.config;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedCounter {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static AtomicInteger getCounter() {
        return counter;
    }
}