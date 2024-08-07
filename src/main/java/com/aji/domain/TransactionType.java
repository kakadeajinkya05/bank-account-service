package com.aji.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public enum TransactionType {

    DEBIT, CREDIT, NONE
}
