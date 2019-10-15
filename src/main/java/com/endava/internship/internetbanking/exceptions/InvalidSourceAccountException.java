package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSourceAccountException extends RuntimeException {
    public InvalidSourceAccountException(String message) {
        super(message);
    }
}
