package com.example.GuitarApp.entity.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SongBeatCreationDto {

    @Size(max = 100, message = "Text must be up to 100 characters")
    private String text;

    @NotNull(message = "Beat should not be null")
    private Integer beat;

    @Size(max = 50, message = "Comment must be up to 50 characters")
    private String comment;

    @NotNull
    @Valid
    @Size(min = 1, message = "Song Beat should contain chords")
    private List<BeatChordCreateDto> beatChords;
}
