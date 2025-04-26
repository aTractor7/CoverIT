package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.util.validators.annotation.NotEqualsField;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class NotEqualsFieldValidator implements ConstraintValidator<NotEqualsField, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(NotEqualsField constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object firstValue = getFieldValue(value, firstFieldName);
        Object secondValue = getFieldValue(value, secondFieldName);

        if(firstValue == null || secondValue == null || firstValue.equals(secondValue)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    private Object getFieldValue(Object value, String fieldName) {
        try {
            Field field = value.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);

            return field.get(value);
        }catch (NoSuchFieldException | IllegalAccessException e){
            return null;
        }
    }
}
