package com.example.GuitarApp.util;

import com.example.GuitarApp.entity.Artist;
import com.example.GuitarApp.entity.Song;
import com.example.GuitarApp.entity.User;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.entity.enums.SongGenre;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class TestDataFactory {
    public static User getUser() {
        User user = new User();
        user.setUsername("john_doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setJoinDate(LocalDate.of(2020, 1, 1));
        user.setRole(Role.USER);
        user.setSkill(Skill.BEGINNER);
        user.setInstrument("Guitar");
        user.setBio("A passionate guitarist.");

        return user;
    }
    public static Song getSongWithAuthor() {
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
        return song;
    }
}
