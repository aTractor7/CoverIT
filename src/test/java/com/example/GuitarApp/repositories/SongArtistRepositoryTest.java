package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SongArtistRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private Song song;
    private Artist artist;

    @BeforeEach
    public void setUp() {
        song = TestDataFactory.getSongWithAuthor();
        artist = song.getSongAuthors().stream().findFirst().get();

        artistRepository.save(artist);
    }

    @Test
    @DisplayName("Save artist with songs and verify ManyToMany relationship")
    void saveArtistWithSongs() {
        // Fetch the artist from the database and verify it exists
        Optional<Artist> optionalArtist = artistRepository.findById(artist.getId());
        assertThat(optionalArtist).isPresent();

        Artist savedArtist = optionalArtist.get();

        // Check that the artist has one song associated
        assertThat(savedArtist.getSongs()).hasSize(1);

        // Validate song properties
        Song savedSong = savedArtist.getSongs().iterator().next();
        assertThat(savedSong.getTitle()).isEqualTo("Bohemian Rhapsody");

        // Fetch the song and verify the reverse side of the relationship
        Optional<Song> optionalSong = songRepository.findById(song.getId());
        assertThat(optionalSong).isPresent();

        Song fetchedSong = optionalSong.get();
        assertThat(fetchedSong.getSongAuthors()).hasSize(1);
        assertThat(fetchedSong.getSongAuthors().iterator().next().getName()).isEqualTo("Queen");
    }
}