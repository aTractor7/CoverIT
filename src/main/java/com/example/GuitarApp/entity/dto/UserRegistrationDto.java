package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.util.validators.annotation.UniqueField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@UniqueField(
        repository = UserRepository.class,
        fieldName = "username",
        message = "{username.unique}"
)
@UniqueField(
        repository = UserRepository.class,
        fieldName = "email",
        message = "{email.unique}"
)
public class UserRegistrationDto{

    @NotBlank(message = "{username.not_blank}")
    @Size(max = 30, message = "{username.size}")
    private String username;

    @NotBlank(message = "{email.not_blank}")
    @Email(message = "{email.valid}")
    @Size(max = 50, message = "{email.size}")
    private String email;

//    TODO: uncomment in prod
//    @ValidPassword
    private String password;

    private Skill skill = Skill.BEGINNER;

    @Size(max = 30, message = "{instrument.size}")
    private String instrument;

    @Size(max = 500, message = "{bio.size}")
    private String bio;
}
