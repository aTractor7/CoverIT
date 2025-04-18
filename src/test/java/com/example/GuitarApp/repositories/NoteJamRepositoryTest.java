package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Jam;
import com.example.GuitarApp.entity.Note;
import com.example.GuitarApp.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NoteJamRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private JamRepository jamRepository;

    private Note note;
    private Jam jam;

    @BeforeEach
    void setUp() {
        jam = TestDataFactory.getJamAndNote();
        note = jam.getJamKey();

        noteRepository.save(note);
        jamRepository.save(jam);
    }

    @Test
    @DisplayName("Check Jam is saved and linked with Note")
    void testJamWithNote() {
        Optional<Jam> jamOpt = jamRepository.findById(jam.getId());
        assertThat(jamOpt).isPresent();

        Jam savedJam = jamOpt.get();
        assertThat(savedJam.getGenre()).isEqualTo(jam.getGenre());
        assertThat(savedJam.getJamKey().getId()).isEqualTo(note.getId());
    }

    @Test
    @DisplayName("Check Note is saved and has Jam mapped")
    void testNoteWithJamMapping() {
        Optional<Note> noteOpt = noteRepository.findById(note.getId());
        assertThat(noteOpt).isPresent();

        Note savedNote = noteOpt.get();
        Set<Jam> jams = savedNote.getJams();
        assertThat(jams.size()).isEqualTo(1);
        assertThat(jams.iterator().next().getId()).isEqualTo(jam.getId());
    }

    @Test
    @DisplayName("Check Jam audio can be stored and retrieved")
    void testJamAudioData() {
        Optional<Jam> jamOpt = jamRepository.findById(jam.getId());
        assertThat(jamOpt).isPresent();

        Jam savedJam = jamOpt.get();
        if (jam.getAudio() != null) {
            assertThat(savedJam.getAudio()).isNotNull();
            assertThat(savedJam.getAudio().length).isEqualTo(jam.getAudio().length);
        } else {
            assertThat(savedJam.getAudio()).isNull();
        }
    }

}