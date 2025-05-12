package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BeatChordDto {

    private int id;

    private int songBeatId;

    @NotBlank(message = "You should set a chord")
    private ChordDto chord;

    private FingeringDto recommendedFingering;
}
