package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NullTransferFundsException extends RuntimeException {
    public NullTransferFundsException(String message) {
        super(message);
    }
}
