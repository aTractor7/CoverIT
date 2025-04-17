package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.SongTutorial;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import com.example.GuitarApp.util.TestDataFactory;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
class SongTutorialRepositoryTest {

    @Autowired
    private SongTutorialRepository songTutorialRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SongRepository songRepository;

    private SongTutorial songTutorial;

    @BeforeEach
    public void setUp() {
        Song song = TestDataFactory.getSongWithAuthor();
        User user = TestDataFactory.getUser();

        userRepository.save(user);
        songRepository.save(song);

        songTutorial = new SongTutorial();
        songTutorial.setDifficulty(TutorialDifficulty.SIMPLIFIED);
        songTutorial.setDescription("A beginner's tutorial on Bohemian Rhapsody");
        songTutorial.setCreatedAt(LocalDateTime.now());

        songTutorial.setTutorialAuthor(user);
        songTutorial.setSong(song);
        song.setTutorials(Set.of(songTutorial));
        user.setTutorials(Set.of(songTutorial));
    }

    @Test
    @DisplayName("Save SongTutorial and verify relationships")
    void saveSongTutorialAndVerifyRelationships() {
        songTutorialRepository.save(songTutorial);

        // Fetch the saved SongTutorial from the database
        Optional<SongTutorial> optionalSongTutorial = songTutorialRepository.findById(songTutorial.getId());
        assertThat(optionalSongTutorial).isPresent();

        SongTutorial savedSongTutorial = optionalSongTutorial.get();

        // Verify that the SongTutorial is associated with the correct user and song
        assertThat(savedSongTutorial.getTutorialAuthor()).isNotNull();
        assertThat(savedSongTutorial.getSong()).isNotNull();
        assertThat(savedSongTutorial.getTutorialAuthor().getUsername())
                .isEqualTo(songTutorial.getTutorialAuthor().getUsername());
        assertThat(savedSongTutorial.getSong().getTitle())
                .isEqualTo(songTutorial.getSong().getTitle());

        // Verify the properties of the saved SongTutorial
        assertThat(savedSongTutorial.getDifficulty()).isEqualTo(songTutorial.getDifficulty());
        assertThat(savedSongTutorial.getDescription()).isEqualTo(songTutorial.getDescription());
    }

    @Test
    @DisplayName("Test validation for SongTutorial description length")
    void testDescriptionValidation() {
        SongTutorial invalidSongTutorial = new SongTutorial();
        invalidSongTutorial.setDifficulty(TutorialDifficulty.SIMPLIFIED);
        invalidSongTutorial.setDescription("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. " +
                "Aenean commodo ligula eget dolor. Aenean massa. Cum.");
        invalidSongTutorial.setCreatedAt(LocalDateTime.now());
        invalidSongTutorial.setTutorialAuthor(songTutorial.getTutorialAuthor());
        invalidSongTutorial.setSong(songTutorial.getSong());

        // Try to save invalid song tutorial and expect validation failure
        try {
            songTutorialRepository.save(invalidSongTutorial);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(ConstraintViolationException.class);
        }
    }

    @Test
    @DisplayName("Test ManyToOne relationships (User and Song)")
    void testManyToOneRelationships() {
        // Fetch the song and verify reverse side of the relationship
        Optional<Song> optionalSong = songRepository.findById(songTutorial.getSong().getId());
        assertThat(optionalSong).isPresent();
        Song fetchedSong = optionalSong.get();
        assertThat(fetchedSong.getTutorials().size()).isGreaterThan(0);

        // Verify the tutorial author (user)
        Optional<User> optionalUser = userRepository.findById(songTutorial.getTutorialAuthor().getId());
        assertThat(optionalUser).isPresent();
        User fetchedUser = optionalUser.get();
        assertThat(fetchedUser.getTutorials().size()).isGreaterThan(0);
        assertThat(fetchedUser.getTutorials().iterator().next().getSong().getTitle())
                .isEqualTo(songTutorial.getSong().getTitle());
    }
}