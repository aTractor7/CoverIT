package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.services.ErrorMessageService;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import com.example.GuitarApp.services.UserService;
import com.example.GuitarApp.util.validators.annotation.ValidOldPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchValidator implements ConstraintValidator<ValidOldPassword, String> {

    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final ErrorMessageService errMsg;

    @Autowired
    public PasswordMatchValidator(UserService userService, UserDetailsServiceImpl userDetailsService, ErrorMessageService errMsg) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.errMsg = errMsg;
    }

    @Override
    public void initialize(ValidOldPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String checked, ConstraintValidatorContext context) {
        if (checked == null || checked.isBlank()) {
            return false;
        }

        UserDetails user = userDetailsService.getCurrentUserDetails();

        if(!userService.matchPassword(checked, user.getPassword())){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            errMsg.getErrorMessage("validation.old_password"))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
