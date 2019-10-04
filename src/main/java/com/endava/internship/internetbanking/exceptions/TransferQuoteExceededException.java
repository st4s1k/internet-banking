package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransferQuoteExceededException extends Exception {
    public TransferQuoteExceededException(String s) {
        super(s);
    }
}
