package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BeatChordCreateDto {

    @NotNull(message = "You should set a chord")
    private ChordShortDto chord;

    private FingeringShortDto recommendedFingering;
}
