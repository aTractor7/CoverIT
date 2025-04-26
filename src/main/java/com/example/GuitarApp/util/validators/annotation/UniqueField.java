package com.example.GuitarApp.util.validators.annotation;

import com.example.GuitarApp.entity.AbstractEntity;
import com.example.GuitarApp.util.validators.UniqueFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UniqueFields.class)
@Constraint(validatedBy = UniqueFieldValidator.class)
public @interface UniqueField {
    String message() default "This field value is already exists";

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

    Class<? extends JpaRepository<? extends AbstractEntity, Integer>> repository();

    String fieldName();

    String idField() default "id";

    boolean canUpdate() default false;
}
