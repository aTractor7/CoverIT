package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ArtistDto {

    private int id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    private String name;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;

    private int creator_id;

    private Set<SongShortDto> songs;
}
