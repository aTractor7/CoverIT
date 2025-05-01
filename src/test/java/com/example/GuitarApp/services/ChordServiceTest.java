package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.repositories.ChordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ChordServiceTest {

    @Mock
    private ChordRepository chordRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @InjectMocks
    private ChordService chordService;

    private Chord testChord;

    @BeforeEach
    void setUp() {
        testChord = new Chord();
        testChord.setId(1);
        testChord.setName("C Major");
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnChord_WhenIdExists() {
            given(chordRepository.findById(1)).willReturn(Optional.of(testChord));

            Chord result = chordService.findOne(1);

            assertThat(result.getName()).isEqualTo(testChord.getName());
        }

        @Test
        void shouldThrow_WhenChordIdNotFound() {
            int id = 42;
            given(chordRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("chord.notfound.byId", id))
                    .willReturn("Chord with id " + id + " not found");

            assertThatThrownBy(() -> chordService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Chord with id 42 not found");
        }

        @Test
        void shouldReturnPageOfChords() {
            Chord secondChord = new Chord();
            secondChord.setName("D Minor");
            Page<Chord> page = new PageImpl<>(Arrays.asList(testChord, secondChord));
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));

            given(chordRepository.findAll(pageable)).willReturn(page);

            List<Chord> result = chordService.findPage(0, 2, Optional.of("name"));

            assertThat(result).hasSize(2).extracting("name")
                    .containsExactly(testChord.getName(), "D Minor");
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateChord() {
            given(chordRepository.save(testChord)).willReturn(testChord);

            Chord result = chordService.create(testChord);

            assertThat(result.getName()).isEqualTo(testChord.getName());
        }

        @Test
        void shouldUpdateChordFields_WhenExists() {
            Chord updated = new Chord();
            updated.setName("G Major");

            given(chordRepository.findById(1)).willReturn(Optional.of(testChord));

            Chord result = chordService.update(1, updated);

            assertThat(result.getName()).isEqualTo("G Major");
        }

        @Test
        void shouldDeleteChordById() {
            chordService.delete(1);

            then(chordRepository).should().deleteById(1);
        }
    }
}
