package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.BeatChord;
import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.Fingering;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.repositories.BeatChordRepository;
import com.example.GuitarApp.util.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BeatChordServiceTest {

    @Mock
    private BeatChordRepository beatChordRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @InjectMocks
    private BeatChordService beatChordService;

    private BeatChord testBeatChord;

    @BeforeEach
    void setUp() {
        SongBeat songBeat = TestDataFactory.getSongBeat();

        testBeatChord = songBeat.getBeatChords().stream().findFirst().get();
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnBeatChord_WhenIdExists() {
            given(beatChordRepository.findById(1)).willReturn(Optional.of(testBeatChord));

            BeatChord result = beatChordService.findOne(1);

            assertThat(result).isEqualTo(testBeatChord);
        }

        @Test
        void shouldThrow_WhenBeatChordNotFound() {
            int id = 42;
            given(beatChordRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("beatChord.notfound.byId", id))
                    .willReturn("BeatChord with id " + id + " not found");

            assertThatThrownBy(() -> beatChordService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("BeatChord with id 42 not found");
        }

        @Test
        void shouldReturnPageOfBeatChords() {
            Pageable pageable = PageRequest.of(0, 2, Sort.by("chord"));
            BeatChord another = new BeatChord();
            another.setChord(new Chord());

            Page<BeatChord> page = new PageImpl<>(List.of(testBeatChord, another));
            given(beatChordRepository.findAll(pageable)).willReturn(page);

            List<BeatChord> result = beatChordService.findPage(0, 2, Optional.of("chord"));

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateBeatChord() {
            given(beatChordRepository.save(testBeatChord)).willReturn(testBeatChord);

            BeatChord result = beatChordService.create(testBeatChord);

            assertThat(result).isEqualTo(testBeatChord);
        }

        @Test
        void shouldUpdateBeatChordFields() {
            BeatChord updated = new BeatChord();
            Chord chord = new Chord();
            chord.setName("Em");
            updated.setChord(chord);
            Fingering fingering = new Fingering();
            fingering.setImgPath("Path");
            fingering.setChord(chord);
            updated.setRecommendedFingering(fingering);

            SongBeat newBeat = new SongBeat();
            newBeat.setId(2);
            updated.setSongBeat(newBeat);

            given(beatChordRepository.findById(1)).willReturn(Optional.of(testBeatChord));

            BeatChord result = beatChordService.update(1, updated);

            assertThat(result.getChord().getName()).isEqualTo(chord.getName());
            assertThat(result.getRecommendedFingering().getImgPath()).isEqualTo(fingering.getImgPath());
            assertThat(result.getSongBeat().getId()).isEqualTo(2);
        }

        @Test
        void shouldDeleteBeatChordById() {
            beatChordService.delete(1);

            then(beatChordRepository).should().deleteById(1);
        }
    }
}
