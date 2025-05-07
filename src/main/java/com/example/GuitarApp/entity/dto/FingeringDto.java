package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FingeringDto {

    private int id;

    @NotBlank(message = "You should set img path.")
    @Size(max = 200, message = "Path to img cannot be longer than 200 characters")
    private String imgPath;

    @NotNull(message = "Can't create fingering without chord.")
    private ChordShortDto chord;
}
