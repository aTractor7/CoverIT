package com.example.GuitarApp.util.validators;

import jakarta.validation.ConstraintValidatorContext;

public class ValidationUtils {

    public static void addFieldViolation(ConstraintValidatorContext context, String fieldName) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(fieldName)
                .addConstraintViolation();
    }
}
