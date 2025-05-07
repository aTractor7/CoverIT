package com.example.GuitarApp.services;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.UserDetailsImpl;
import com.example.GuitarApp.repositories.PersonalLibraryRepository;
import com.example.GuitarApp.util.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;
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
class PersonalLibraryServiceTest {

    @Mock
    private PersonalLibraryRepository personalLibraryRepository;

    @Mock
    private ErrorMessageService errorMessageService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private PersonalLibraryService personalLibraryService;

    private PersonalLibrary testLibrary;

    @BeforeEach
    void setUp() {
        User user = TestDataFactory.getUser();
        SongTutorial tutorial = TestDataFactory.getSongTutorial();

        testLibrary = TestDataFactory.getPersonalLibrary();
        testLibrary.setId(1);
        testLibrary.setOwner(user);
        testLibrary.setSongTutorial(tutorial);
    }

    @Nested
    @DisplayName("Find tests")
    class FindTests {

        @Test
        void shouldReturnPersonalLibrary_WhenIdExists() {
            given(personalLibraryRepository.findById(1)).willReturn(Optional.of(testLibrary));

            PersonalLibrary result = personalLibraryService.findOne(1);

            assertThat(result.getSongTutorial()).isEqualTo(testLibrary.getSongTutorial());
        }

        @Test
        void shouldThrow_WhenPersonalLibraryNotFound() {
            int id = 42;
            given(personalLibraryRepository.findById(id)).willReturn(Optional.empty());
            given(errorMessageService.getErrorMessage("personalLibrary.notfound.byId", id))
                    .willReturn("Library with id " + id + " not found");

            assertThatThrownBy(() -> personalLibraryService.findOne(id))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Library with id 42 not found");
        }

        @Test
        void shouldReturnPageOfPersonalLibraries() {
            PersonalLibrary second = new PersonalLibrary();
            second.setId(2);
            second.setSongTutorial(testLibrary.getSongTutorial());
            second.setOwner(testLibrary.getOwner());

            Page<PersonalLibrary> page = new PageImpl<>(Arrays.asList(testLibrary, second));
            Pageable pageable = PageRequest.of(0, 2, Sort.by("songTutorial"));

            given(personalLibraryRepository.findAll(pageable)).willReturn(page);

            List<PersonalLibrary> result = personalLibraryService.findPage(0, 2, Optional.of("songTutorial"));

            assertThat(result).hasSize(2)
                    .extracting("songTutorial")
                    .containsExactly(testLibrary.getSongTutorial(), testLibrary.getSongTutorial());
        }
    }

    @Nested
    @DisplayName("Create / Update / Delete tests")
    class CrudTests {

        @Test
        void shouldCreatePersonalLibraryWithOwner() {
            UserDetailsImpl userDetails = Mockito.mock(UserDetailsImpl.class);
            when(userDetails.getId()).thenReturn(1);

            given(userDetailsService.getCurrentUserDetails()).willReturn(userDetails);

            given(personalLibraryRepository.save(any(PersonalLibrary.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            PersonalLibrary result = personalLibraryService.create(testLibrary);

            assertThat(result.getOwner().getId()).isEqualTo(1);
        }

        @Test
        void shouldUpdateSongTutorial_WhenLibraryExists() {
            PersonalLibrary updated = new PersonalLibrary();
            updated.setId(1);
            testLibrary.getSongTutorial().setDescription("Updated Description");
            updated.setSongTutorial(testLibrary.getSongTutorial());
            updated.setOwner(testLibrary.getOwner());

            given(personalLibraryRepository.findById(1)).willReturn(Optional.of(testLibrary));

            PersonalLibrary result = personalLibraryService.update(1, updated);

            assertThat(result.getSongTutorial().getDescription()).isEqualTo("Updated Description");
        }

        @Test
        void shouldDeletePersonalLibraryById() {
            personalLibraryService.delete(1);

            then(personalLibraryRepository).should().deleteById(1);
        }
    }
}
