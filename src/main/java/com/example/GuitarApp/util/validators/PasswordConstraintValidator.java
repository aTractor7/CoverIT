package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.util.validators.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final ErrorMessageService errMsg;

    public PasswordConstraintValidator(ErrorMessageService errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.isBlank()) {
            return false;
        }

        boolean lengthOk = password.length() >= 8 && password.length() <= 100;
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);

        if (!(lengthOk && hasDigit && hasUpper)) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            errMsg.getErrorMessage("validation.password"))
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}
