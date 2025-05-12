package com.example.GuitarApp.util.validators.annotation;

import com.example.GuitarApp.util.validators.NotEqualsFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotEqualsFieldValidator.class)
@Documented
public @interface NotEqualsField {

    String message() default "Fields must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String first();
    String second();
}
