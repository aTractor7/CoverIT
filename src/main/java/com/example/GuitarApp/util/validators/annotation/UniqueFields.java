package com.example.GuitarApp.util.validators.annotation;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueFields {
    UniqueField[] value();
}
