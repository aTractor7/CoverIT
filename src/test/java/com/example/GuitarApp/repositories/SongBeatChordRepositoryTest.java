package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.*;
import com.example.GuitarApp.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class SongBeatChordRepositoryTest {

    @Autowired
    private SongBeatRepository songBeatRepository;
    @Autowired
    private FingeringRepository fingeringRepository;
    @Autowired
    private ChordRepository chordRepository;
    @Autowired
    private BeatChordRepository beatChordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongTutorialRepository songTutorialRepository;

    private Chord chord;
    private Fingering fingering;
    private SongBeat songBeat;

    @BeforeEach
    void setUp() {
        Song song = TestDataFactory.getSongWithAuthor();
        User user = TestDataFactory.getUser();

        userRepository.save(user);
        songRepository.save(song);

        SongTutorial tutorial = TestDataFactory.getSongTutorial();

        tutorial.setTutorialAuthor(user);
        tutorial.setSong(song);
        song.setTutorials(Set.of(tutorial));
        user.setTutorials(Set.of(tutorial));

        songTutorialRepository.save(tutorial);

        songBeat = TestDataFactory.getSongBeat();
        BeatChord beatChord = songBeat.getBeatChords().iterator().next();
        chord = beatChord.getChord();
        fingering = beatChord.getRecommendedFingering();

        songBeat.setSongTutorial(tutorial);

        chordRepository.save(chord);
        chord.setFingerings(List.of(fingering));
        fingeringRepository.save(fingering);
        songBeatRepository.save(songBeat);
        beatChordRepository.save(beatChord);
    }

    @Test
    @DisplayName("Check SongBeat and BeatChord relation")
    void testSongBeatWithBeatChord() {
        Optional<SongBeat> beatOpt = songBeatRepository.findById(songBeat.getId());
        assertThat(beatOpt).isPresent();
        SongBeat loadedBeat = beatOpt.get();

        assertThat(loadedBeat.getBeatChords().size()).isEqualTo(1);
        BeatChord bc = loadedBeat.getBeatChords().iterator().next();
        assertThat(bc.getChord().getName()).isEqualTo("Am");
    }

    @Test
    @DisplayName("Check Chord and Fingering and BeatChord relation")
    void testChordFingeringBeatChordRelation() {
        Optional<Chord> chordOpt = chordRepository.findById(chord.getId());
        assertThat(chordOpt).isPresent();

        Chord savedChord = chordOpt.get();
        assertThat(savedChord.getFingerings().size()).isEqualTo(1);
        assertThat(savedChord.getBeatChords().size()).isEqualTo(1);

        BeatChord bc = savedChord.getBeatChords().iterator().next();
        assertThat(bc.getRecommendedFingering().getId()).isEqualTo(fingering.getId());
    }

    @Test
    @DisplayName("Check Fingering recommendedFor relation")
    void testFingeringRecommendedFor() {
        Optional<Fingering> fingeringOpt = fingeringRepository.findById(fingering.getId());
        assertThat(fingeringOpt).isPresent();

        Fingering savedFingering = fingeringOpt.get();
        assertThat(savedFingering.getRecommendedFor().size()).isEqualTo(1);
        BeatChord bc = savedFingering.getRecommendedFor().iterator().next();
        assertThat(bc.getChord().getName()).isEqualTo("Am");
    }

}