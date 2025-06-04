package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SongTutorialCreateDto {

    @NotNull(message = "Difficulty cannot be null")
    private TutorialDifficulty difficulty;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String description;

    private byte[] backtrack;

    @Size(max = 20, message = "Recommended strumming cannot be longer than 20 characters")
    private String recommendedStrumming;

    @NotNull
    private SongShortDto song;

    @NotNull
    @Valid
    private List<SongBeatCreationDto> beats;
}
