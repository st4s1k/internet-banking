package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InsufficientTransferFundsException extends Exception {
    public InsufficientTransferFundsException(String s) {
        super(s);
    }
}
