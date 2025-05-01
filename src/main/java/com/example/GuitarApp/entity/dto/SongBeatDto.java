package com.example.GuitarApp.entity.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SongBeatDto {

    private int id;

    @Size(max = 100, message = "Text must be up to 100 characters")
    private String text;

    @NotNull(message = "Beat must not be null")
    private Integer beat;

    private int songTutorialId;

    @NotNull
    @Size(min = 1, message = "Song Beat should contain chords")
    private Set<BeatChordDto> beatChords;
}
