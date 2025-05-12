package com.example.GuitarApp.util.validators;

import com.example.GuitarApp.entity.*;
import com.example.GuitarApp.entity.dto.*;
import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import com.example.GuitarApp.repositories.ChordRepository;
import com.example.GuitarApp.repositories.FingeringRepository;
import com.example.GuitarApp.repositories.SongRepository;
import com.example.GuitarApp.repositories.SongTutorialRepository;
import com.example.GuitarApp.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SongTutorialValidator {

    private final SongTutorialRepository songTutorialRepository;
    private final ChordRepository chordRepository;
    private final FingeringRepository fingeringRepository;
    private final SongRepository songRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SongTutorialValidator(SongTutorialRepository songTutorialRepository, ChordRepository chordRepository, FingeringRepository fingeringRepository, SongRepository songRepository, UserDetailsServiceImpl userDetailsService) {
        this.songTutorialRepository = songTutorialRepository;
        this.chordRepository = chordRepository;
        this.fingeringRepository = fingeringRepository;
        this.songRepository = songRepository;
        this.userDetailsService = userDetailsService;
    }


    //TODO: додать валідацію на співпадіння акорда і фінгерінга
    public boolean validate(SongTutorialCreateDto tutorialCreateDto, Errors errors, boolean update) {
        SongShortDto songDto = tutorialCreateDto.getSong();

        Song songDb = songRepository.findByTitle(songDto.getTitle()).orElse(null);

        if(songDb == null
                || songDb.getId() != tutorialCreateDto.getSong().getId()) {
            errors.rejectValue("song", "400", "No song with such title found or wrong id");
            return false;
        }
        /// ///////////////////////////////////////////////////////////////////////

        List<BeatChordCreateDto> beatsChords = tutorialCreateDto.getBeats().stream()
                .map(SongBeatCreationDto::getBeatChords)
                .flatMap(List::stream)
                .toList();

        Set<Integer> chordsId = beatsChords.stream()
                .map(BeatChordCreateDto::getChord)
                .map(ChordShortDto::getId)
                .collect(Collectors.toSet());

        Set<Chord> chordsDb = chordRepository.findByIdIn(chordsId);

        if (chordsDb.size() != chordsId.size()) {
            errors.rejectValue("beats", "400", "No chord with such id found");
            return false;
        }

/// ////////////////////////////////////////////////////////////////////////////////
        Set<Integer> fingeringIds = beatsChords.stream()
                .map(BeatChordCreateDto::getRecommendedFingering)
                .filter(Objects::nonNull)
                .map(FingeringShortDto::getId)
                .collect(Collectors.toSet());

        if(!fingeringIds.isEmpty() && !fingeringIds.stream().allMatch(Objects::isNull)) {
            Set<Fingering> fingeringsDb = fingeringRepository.findByIdIn(fingeringIds);

            if (fingeringsDb.size() != fingeringIds.size()) {
                errors.rejectValue("beats", "400", "No fingering with such id found");
            }
        }
/// ////////////////////////////////////////////////////////////////////////////////
        Set<SongTutorial> songTutorials = songTutorialRepository.findAllByTutorialAuthor(
                userDetailsService.getCurrentUserDetails().user());

        boolean existsForUser = songTutorials.stream()
                .anyMatch(t -> t.getSong().getId() == songDto.getId());

        if(update) return true;

        if(existsForUser) {
            errors.rejectValue("song", "400", "You can`t create second tutorial on same song");
            return false;
        }

        return true;
    }
}
