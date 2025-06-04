package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FingeringShortDto {

    private int id;

    @NotBlank(message = "You should set img path.")
    private String imgPath;

    private ChordShortDto chordShort;
}
