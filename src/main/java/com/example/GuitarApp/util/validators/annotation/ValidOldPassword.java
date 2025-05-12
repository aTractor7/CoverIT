package com.example.GuitarApp.util.validators.annotation;

import com.example.GuitarApp.util.validators.PasswordMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOldPassword {
    String message() default "Old password is incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
