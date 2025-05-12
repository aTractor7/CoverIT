package com.example.GuitarApp.util.exceptions;

import jakarta.validation.ValidationException;

public class UniqueFieldValidatorConfigurationException extends ValidationException {
    public UniqueFieldValidatorConfigurationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
