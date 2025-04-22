package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDto {

    String oldPassword;

    //TODO: add custom annotation validation on password
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 100, message = "Password should be at least 8 characters. Or less then 100")
    String newPassword;
}
