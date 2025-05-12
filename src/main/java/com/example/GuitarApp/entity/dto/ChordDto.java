package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.repositories.ChordRepository;
import com.example.GuitarApp.util.validators.annotation.UniqueField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
@UniqueField(
        repository = ChordRepository.class,
        fieldName = "name",
        canUpdate = true,
        message = "{chord.unique}"
)
public class ChordDto {

    private int id;

    @NotBlank(message = "Chord name must not be empty")
    @Size(max = 10, message = "Chord name must be at most 10 characters")
    private String name;

    private Set<FingeringShortDto> fingerings;
}
