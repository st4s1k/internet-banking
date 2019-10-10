package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InsufficientSourceFundsException extends RuntimeException {
    public InsufficientSourceFundsException(String s) {
        super(s);
    }
}
