package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalLibraryCreateDto {
    @NotNull
    private Integer tutorialId;
}
