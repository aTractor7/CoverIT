package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.Skill;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 30, message = "Username cannot be longer than 30 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email cannot be longer than 50 characters")
    private String email;

    //TODO: add custom annotation validation on password
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 100, message = "Password should be at least 8 characters. Or less then 100")
    private String password;

    private Skill skill = Skill.BEGINNER;

    @Size(max = 30, message = "Instrument cannot be longer than 30 characters")
    private String instrument;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;
}
