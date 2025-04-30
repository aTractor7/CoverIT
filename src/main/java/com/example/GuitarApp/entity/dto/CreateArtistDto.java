package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.repositories.ArtistRepository;
import com.example.GuitarApp.repositories.UserRepository;
import com.example.GuitarApp.util.validators.annotation.UniqueField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@UniqueField(
        repository = ArtistRepository.class,
        fieldName = "name",
        canUpdate = true,
        message = "{artist.name.unique}"
)
public class CreateArtistDto {

    private int id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    private String name;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;
}
