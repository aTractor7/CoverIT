package com.example.GuitarApp.repositories;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.enums.SongGenre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SongArtistRepositoryTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    @DisplayName("Save artist with songs and verify ManyToMany relationship")
    void saveArtistWithSongs() {
        // Create a new Song instance
        Song song = new Song();
        song.setTitle("Bohemian Rhapsody");
        song.setGenre(SongGenre.ROCK);
        song.setReleaseDate(LocalDate.of(1975, 11, 21));

        // Create a new Artist instance
        Artist artist = new Artist();
        artist.setName("Queen");
        artist.setBio("Legendary British rock band");

        // Set bidirectional ManyToMany relationship
        Set<Song> songs = new HashSet<>();
        songs.add(song);
        artist.setSongs(songs);

        Set<Artist> authors = new HashSet<>();
        authors.add(artist);
        song.setSongAuthors(authors);

        // Save artist (cascades and saves the song as well)
        artistRepository.save(artist);

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