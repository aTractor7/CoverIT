package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Chord;
import com.example.GuitarApp.entity.SongBeat;
import com.example.GuitarApp.repositories.SongBeatRepository;
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
class SongBeatServiceTest {

    @Mock
    private SongBeatRepository songBeatRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @InjectMocks
    private SongBeatService songBeatService;

    private SongBeat songBeat;

    @BeforeEach
    void setUp() {
        songBeat = TestDataFactory.getSongBeat();
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnSongBeat_WhenIdExists() {
            given(songBeatRepository.findById(1)).willReturn(Optional.of(songBeat));

            SongBeat result = songBeatService.findOne(1);

            assertThat(result).isEqualTo(songBeat);
        }

        @Test
        void shouldThrow_WhenSongBeatNotFound() {
            int id = 42;
            given(songBeatRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("songBeat.notfound.byId", id))
                    .willReturn("SongBeat with id " + id + " not found");

            assertThatThrownBy(() -> songBeatService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("SongBeat with id 42 not found");
        }

        @Test
        void shouldReturnPageOfSongBeats() {
            SongBeat another = new SongBeat();
            another.setId(2);

            Pageable pageable = PageRequest.of(0, 2, Sort.by("text"));
            Page<SongBeat> page = new PageImpl<>(List.of(songBeat, another));

            given(songBeatRepository.findAll(pageable)).willReturn(page);

            List<SongBeat> result = songBeatService.findPage(0, 2, Optional.of("text"));

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("CRUD tests")
    class CrudTests {

        @Test
        void shouldCreateSongBeat() {
            given(songBeatRepository.save(songBeat)).willReturn(songBeat);

            SongBeat result = songBeatService.create(songBeat);

            assertThat(result).isEqualTo(songBeat);
        }

        @Test
        void shouldUpdateSongBeatFields() {
            SongBeat updated = new SongBeat();
            updated.setText("Updated text");

            given(songBeatRepository.findById(1)).willReturn(Optional.of(songBeat));

            SongBeat result = songBeatService.update(1, updated);

            assertThat(result.getText()).isEqualTo("Updated text");
        }

        @Test
        void shouldDeleteSongBeatById() {
            songBeatService.delete(1);

            then(songBeatRepository).should().deleteById(1);
        }
    }
}
