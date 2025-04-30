package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.util.validators.annotation.UniqueField;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//TODO: не працюють імпорти повідомлень

@Data
@NoArgsConstructor
@AllArgsConstructor
@UniqueField(
        repository = UserRepository.class,
        fieldName = "username",
        canUpdate = true,
        message = "{username.unique}"
)
@UniqueField(
        repository = UserRepository.class,
        fieldName = "email",
        canUpdate = true,
        message = "{email.unique}"
)
public class UserDto{

    @NotNull
    private Integer id;

    @NotBlank(message = "{username.not_blank}")
    @Size(max = 30, message = "{username.size}")
    private String username;

    @NotBlank(message = "{email.not_blank}")
    @Email(message = "{email.valid}")
    @Size(max = 50, message = "{email.size}")
    private String email;

    private byte[] profileImg;
    private LocalDate joinDate;
    private Role role;

    @NotNull(message = "{skill.not_null}")
    private Skill skill;

    @Size(max = 30, message = "{instrument.size}")
    private String instrument;

    @Size(max = 500, message = "{bio.size}")
    private String bio;
}
