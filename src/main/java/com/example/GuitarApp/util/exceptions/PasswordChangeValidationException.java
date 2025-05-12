package com.example.GuitarApp.util.exceptions;

import org.springframework.validation.FieldError;

public class PasswordChangeValidationException extends RuntimeException {

    public PasswordChangeValidationException(String message) {
        super(message);
    }
}
