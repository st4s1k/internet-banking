package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransferFailedException extends RuntimeException {
    public TransferFailedException(String message) {
        super(message);
    }
}
