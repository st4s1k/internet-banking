package com.endava.internship.internetbanking.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidSourceAccountException extends Exception {
    public InvalidSourceAccountException(String s) {
        super(s);
    }
}
