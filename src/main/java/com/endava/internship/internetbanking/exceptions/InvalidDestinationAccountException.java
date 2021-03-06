package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDestinationAccountException extends RuntimeException {
    public InvalidDestinationAccountException(String message) {
        super(message);
    }
}
