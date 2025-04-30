package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SongShortDto {
    private int id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title cannot be longer than 50 characters")
    private String title;
}
