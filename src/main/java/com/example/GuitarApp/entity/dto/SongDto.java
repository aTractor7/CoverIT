package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.SongGenre;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class SongDto {

    private int id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title cannot be longer than 50 characters")
    private String title;

    @NotNull(message = "Genre cannot be empty")
    private SongGenre genre;

    //TODO: додать юнік на тайтл + дату
    //TODO: покумекать над форматом
    @NotNull(message = "Release date is required")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate releaseDate;

    private int creator_id;

    @NotNull
    @Size(min = 1, message = "Song must have at least one artist")
    private Set<ArtistShortDto> songAuthors;
}
