package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.ArtistRepository;
import com.example.GuitarApp.repositories.SongRepository;
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
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private SongService songService;

    private Song testSong;

    @BeforeEach
    void setUp() {
        testSong = TestDataFactory.getSongWithAuthor();

        testSong.setCreatedBy(TestDataFactory.getUser());
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnSong_WhenIdExists() {
            given(songRepository.findById(1)).willReturn(Optional.of(testSong));

            Song result = songService.findOne(1);

            assertThat(result.getTitle()).isEqualTo(testSong.getTitle());
        }

        @Test
        void shouldThrow_WhenSongIdNotFound() {
            int id = 100;
            given(songRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("song.notfound.byId", id))
                    .willReturn("Song with id " + id + " not found");

            assertThatThrownBy(() -> songService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Song with id 100 not found");
        }

        @Test
        void shouldReturnSong_WhenTitleExists() {
            given(songRepository.findByTitle(testSong.getTitle())).willReturn(Optional.of(testSong));

            Song result = songService.findOne(testSong.getTitle());

            assertThat(result.getTitle()).isEqualTo(testSong.getTitle());
        }

        @Test
        void shouldThrow_WhenSongTitleNotFound() {
            String title = "Unknown Song";
            given(songRepository.findByTitle(title)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("song.notfound.byTitle", title))
                    .willReturn("Song with title " + title + " not found");

            assertThatThrownBy(() -> songService.findOne(title))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Song with title Unknown Song not found");
        }

        @Test
        void shouldReturnPageOfSongs() {
            Song another = new Song();
            another.setTitle("Another");

            Pageable pageable = PageRequest.of(0, 2, Sort.by("title"));
            Page<Song> page = new PageImpl<>(Arrays.asList(testSong, another));

            given(songRepository.findAll(pageable)).willReturn(page);

            List<Song> result = songService.findPage(0, 2, Optional.of("title"));

            assertThat(result).hasSize(2).extracting("title")
                    .containsExactly(testSong.getTitle(), "Another");
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateSong() {
            given(songRepository.save(testSong)).willReturn(testSong);

            UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
            when(userDetails.getId()).thenReturn(testSong.getCreatedBy().getId());
            when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);

            Song result = songService.create(testSong);

            assertThat(result.getTitle()).isEqualTo(testSong.getTitle());
        }

        @Test
        void shouldUpdateSongFields_WhenExists() {
            Song updated = new Song();
            updated.setTitle("Updated Title");
            updated.setGenre(testSong.getGenre());
            updated.setReleaseDate(testSong.getReleaseDate());
            updated.setSongAuthors(new HashSet<>(testSong.getSongAuthors()));

            given(songRepository.findById(1)).willReturn(Optional.of(testSong));

            Song result = songService.update(1, updated);

            assertThat(result.getTitle()).isEqualTo("Updated Title");
        }

        @Test
        void shouldDeleteSongById() {
            songService.delete(1);

            then(songRepository).should().deleteById(1);
        }
    }
}
