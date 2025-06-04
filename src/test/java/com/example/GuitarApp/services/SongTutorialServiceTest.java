package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.*;
import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import com.example.GuitarApp.repositories.SongBeatRepository;
import com.example.GuitarApp.repositories.SongTutorialRepository;
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
class SongTutorialServiceTest {

    @Mock
    private SongTutorialRepository tutorialRepository;

    @Mock
    private SongBeatRepository songBeatRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private SongTutorialService tutorialService;

    private SongTutorial tutorial;
    private Song song;
    private User user;

    @BeforeEach
    void setUp() {
        user = TestDataFactory.getUser();
        song = TestDataFactory.getSongWithAuthor();
        song.setCreatedBy(user);

        tutorial = TestDataFactory.getSongTutorial();
        tutorial.setSong(song);
        tutorial.setTutorialAuthor(user);
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnTutorialById() {
            given(tutorialRepository.findById(1)).willReturn(Optional.of(tutorial));

            SongTutorial result = tutorialService.findOne(1);

            assertThat(result.getDescription()).isEqualTo(tutorial.getDescription());
        }

        @Test
        void shouldThrowIfTutorialNotFound() {
            int id = 42;
            given(tutorialRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("songTutorial.notfound.byId", id)).willReturn("Not found");

            assertThatThrownBy(() -> tutorialService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Not found");
        }

        @Test
        void shouldReturnPageOfTutorials() {
            Page<SongTutorial> page = new PageImpl<>(List.of(tutorial));
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
            given(tutorialRepository.findAll(pageable)).willReturn(page);

            List<SongTutorial> result = tutorialService.findPage(0, 10, Optional.of("id"));

            assertThat(result).hasSize(1);
        }

        @Test
        void shouldReturnFilteredPageByTitle() {
            Page<SongTutorial> page = new PageImpl<>(List.of(tutorial));
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
            given(tutorialRepository.findBySongTitleContainingIgnoreCase("Test", pageable)).willReturn(page);

            List<SongTutorial> result = tutorialService.findPage(0, 10, Optional.of("id"), Optional.of("Test"));

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateTutorialWithCurrentUser() {
            UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
            when(userDetails.getId()).thenReturn(user.getId());
            when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);

            given(tutorialRepository.save(any())).willReturn(tutorial);

            SongTutorial created = tutorialService.create(tutorial);

            assertThat(created.getTutorialAuthor().getId()).isEqualTo(0);
        }

        @Test
        void shouldUpdateTutorialFields() {
            SongTutorial updated = new SongTutorial();
            updated.setDescription("New desc");
            updated.setDifficulty(TutorialDifficulty.ORIGINAL);
            updated.setRecommendedStrumming("UpDown");
            updated.setSong(song);
            updated.setSongBeats(tutorial.getSongBeats());

            List<SongBeat> beats = List.of(new SongBeat());
            updated.setSongBeats(beats);

            given(tutorialRepository.findById(1)).willReturn(Optional.of(tutorial));

            SongTutorial result = tutorialService.update(1, updated);

            assertThat(result.getDescription()).isEqualTo("New desc");
        }

        @Test
        void shouldDeleteTutorial() {
            tutorialService.delete(1);

            then(tutorialRepository).should().deleteById(1);
        }
    }
}