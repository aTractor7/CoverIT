package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class PasswordValidator {

    private final UserService userService;

    @Autowired
    public PasswordValidator(UserService userService) {
        this.userService = userService;
    }

    public void validate(String oldPasswordInput, String newPassword, UserDetails userDetails, Errors errors) {
        if(!userService.matchPassword(oldPasswordInput, userDetails.getPassword())) {
            errors.rejectValue("oldPassword", "400", "Wrong password");
        }

        if(newPassword.equals(oldPasswordInput)) {
            errors.rejectValue("oldPassword", "400", "New password is the same");
        }
    }
}
