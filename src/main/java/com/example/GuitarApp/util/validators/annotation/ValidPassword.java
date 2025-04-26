package com.example.GuitarApp.util.validators.annotation;

import com.example.GuitarApp.util.validators.PasswordConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password does not meet the requirements";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
