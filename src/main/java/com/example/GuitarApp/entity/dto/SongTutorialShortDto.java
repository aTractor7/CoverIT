package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SongTutorialShortDto {

    private int id;

    private UserDto tutorialAuthor;

    @NotNull
    private SongShortDto song;
}
