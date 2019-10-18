package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransferLoggingException extends RuntimeException {
    public TransferLoggingException(String message) {
        super(message);
    }
}
