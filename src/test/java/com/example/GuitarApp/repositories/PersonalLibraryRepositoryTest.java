package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.PersonalLibrary;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
class PersonalLibraryRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private SongTutorialRepository tutorialRepository;
    @Autowired
    private PersonalLibraryRepository libraryRepository;

    private PersonalLibrary library;

    @BeforeEach
    void setUp() {
        Song song = TestDataFactory.getSongWithAuthor();
        User user = TestDataFactory.getUser();

        userRepository.save(user);
        songRepository.save(song);

        SongTutorial songTutorial = TestDataFactory.getSongTutorial();

        songTutorial.setTutorialAuthor(user);
        songTutorial.setSong(song);
        song.setTutorials(Set.of(songTutorial));
        user.setTutorials(Set.of(songTutorial));

        tutorialRepository.save(songTutorial);

        library = TestDataFactory.getPersonalLibrary();

        library.setOwner(user);
        user.setLibrary(Set.of(library));
        library.setSongTutorial(songTutorial);
        songTutorial.setPersonalLibraries(Set.of(library));

        libraryRepository.save(library);
    }

    @Test
    @DisplayName("Should find all personal library entries")
    void shouldFindAllPersonalLibraryEntries() {
        List<PersonalLibrary> all = libraryRepository.findAll();
        assertThat(all).hasSize(1);
        assertThat(all.get(0).getOwner().getId()).isEqualTo(library.getOwner().getId());
    }

    @Test
    @DisplayName("Should find personal library by id")
    void shouldFindPersonalLibraryById() {
        Optional<PersonalLibrary> found = libraryRepository.findById(library.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getSongTutorial().getId()).isEqualTo(library.getSongTutorial().getId());
    }

    @Test
    @DisplayName("Shouldn't save duplicate personal library")
    void shouldSaveNewPersonalLibrary() {
        PersonalLibrary newLibrary = TestDataFactory.getPersonalLibrary();
        newLibrary.setOwner(library.getOwner());
        newLibrary.setSongTutorial(library.getSongTutorial());

        try {
            libraryRepository.save(newLibrary);
        }catch (Exception e){
            assertThat(e).isInstanceOf(DataIntegrityViolationException.class);
        }
    }
}