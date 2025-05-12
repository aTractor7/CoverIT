package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.dto.FingeringDto;
import com.example.GuitarApp.repositories.ChordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.Optional;

@Component
public class ExistsChordValidator {

    private final ChordRepository chordRepository;

    @Autowired
    public ExistsChordValidator(ChordRepository chordRepository) {
        this.chordRepository = chordRepository;
    }

    public boolean validate(FingeringDto fingeringDto, Errors errors) {
        if(fingeringDto == null || fingeringDto.getChord() == null)
            return true;

        String chordsName = fingeringDto.getChord().getName();

        Optional<Chord> found = chordRepository.findByName(chordsName);

        if (found.isPresent() && found.get().getId() == fingeringDto.getChord().getId()) return true;

        errors.rejectValue("chord", "400", "No song chord with such name found or wrong chord id");
        return false;
    }
}
