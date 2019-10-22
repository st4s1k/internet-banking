package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransferQuotaExceededException extends RuntimeException {
    public TransferQuotaExceededException(String message) {
        super(message);
    }
}
