package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FingeringDto {

    private int id;

    @NotBlank(message = "You should set img path.")
    private String imgPath;

    @NotNull(message = "Can't create fingering without chord.")
    private ChordShortDto chord;
}
