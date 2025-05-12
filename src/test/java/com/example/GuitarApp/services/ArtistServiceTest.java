package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.ArtistRepository;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private ArtistService artistService;

    private Artist testArtist;

    @BeforeEach
    void setUp() {
        testArtist = TestDataFactory.getSongWithAuthor()
                .getSongAuthors().stream()
                .findFirst()
                .get();
        testArtist.setCreatedBy(TestDataFactory.getUser());
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnArtist_WhenIdExists() {
            given(artistRepository.findById(1)).willReturn(Optional.of(testArtist));

            Artist result = artistService.findOne(1);

            assertThat(result.getName()).isEqualTo(testArtist.getName());
        }

        @Test
        void shouldThrow_WhenArtistIdNotFound() {
            int id = 42;
            given(artistRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("artist.notfound.byId", id))
                    .willReturn("Artist with id " + id + " not found");

            assertThatThrownBy(() -> artistService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Artist with id 42 not found");
        }

        @Test
        void shouldReturnArtist_WhenNameExists() {
            given(artistRepository.findByName(testArtist.getName())).willReturn(Optional.of(testArtist));

            Artist result = artistService.findOne(testArtist.getName());

            assertThat(result.getBio()).isEqualTo(testArtist.getBio());
        }

        @Test
        void shouldThrow_WhenArtistNameNotFound() {
            String name = "Unknown";
            given(artistRepository.findByName(name)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("artist.notfound.byName", name))
                    .willReturn("Artist with name " + name + " not found");

            assertThatThrownBy(() -> artistService.findOne(name))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Artist with name Unknown not found");
        }

        @Test
        void shouldReturnPageOfArtists() {
            Artist secondArtist = new Artist();
            secondArtist.setName("Second");
            Page<Artist> page = new PageImpl<>(Arrays.asList(testArtist, secondArtist));
            Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));

            given(artistRepository.findAll(pageable)).willReturn(page);

            List<Artist> result = artistService.findPage(0, 2, Optional.of("name"));

            assertThat(result).hasSize(2).extracting("name")
                    .containsExactly(testArtist.getName(), "Second");
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreateArtist() {
            given(artistRepository.save(testArtist)).willReturn(testArtist);

            UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
            when(userDetails.getId()).thenReturn(testArtist.getCreatedBy().getId());
            when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);

            Artist result = artistService.create(testArtist);

            assertThat(result.getName()).isEqualTo(testArtist.getName());
        }

        @Test
        void shouldUpdateArtistFields_WhenExists() {
            Artist updated = new Artist();
            updated.setName("Updated Name");
            updated.setBio("Updated Bio");

            given(artistRepository.findById(1)).willReturn(Optional.of(testArtist));

            Artist result = artistService.update(1, updated);

            assertThat(result.getName()).isEqualTo("Updated Name");
            assertThat(result.getBio()).isEqualTo("Updated Bio");
        }

        @Test
        void shouldDeleteArtistById() {
            artistService.delete(1);

            then(artistRepository).should().deleteById(1);
        }
    }
}
