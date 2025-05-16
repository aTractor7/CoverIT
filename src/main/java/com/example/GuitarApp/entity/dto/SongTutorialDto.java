package com.example.GuitarApp.entity.dto;


import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class SongTutorialDto {

    private int id;

    @NotNull(message = "Difficulty cannot be null")
    private TutorialDifficulty difficulty;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    private String description;

    private byte[] backtrack;

    //TODO: покумекать над форматом
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    @Size(max = 20, message = "Recommended strumming cannot be longer than 20 characters")
    private String recommendedStrumming;

    private UserDto tutorialAuthor;

    @NotNull
    private SongShortDto song;

    @Valid
    private Set<CommentDto> comments;

    @NotNull
    @Valid
    private List<SongBeatDto> beats;
}
