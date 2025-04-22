package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 30, message = "Username cannot be longer than 30 characters")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email cannot be longer than 50 characters")
    private String email;

    private byte[] profileImg;
    private LocalDate joinDate;
    private Role role;

    @NotNull(message = "Skill cannot be null")
    private Skill skill;

    @Size(max = 30, message = "Instrument cannot be longer than 30 characters")
    private String instrument;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;
}
