package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidDestinationAccountException extends Exception {
    public InvalidDestinationAccountException(String s) {
        super(s);
    }
}
