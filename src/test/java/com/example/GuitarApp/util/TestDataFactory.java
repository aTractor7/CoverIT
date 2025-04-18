package com.example.GuitarApp.util;

import com.example.GuitarApp.entity.*;
import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import com.example.GuitarApp.entity.enums.SongGenre;
import com.example.GuitarApp.entity.enums.TutorialDifficulty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestDataFactory {
    public static User getUser() {
        return getUser("john.doe");
    }

    public static User getUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username+ "@example.com");
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

    public static SongTutorial getSongTutorial() {
        SongTutorial songTutorial = new SongTutorial();
        songTutorial.setDifficulty(TutorialDifficulty.SIMPLIFIED);
        songTutorial.setDescription("A beginner's tutorial on Bohemian Rhapsody");
        songTutorial.setCreatedAt(LocalDateTime.now());

        return songTutorial;
    }

    public static List<Comment> getComments() {
        Comment comment1 = new Comment();
        comment1.setText("Great tutorial! This really helped me.");

        Comment comment2 = new Comment();
        comment2.setText("I agree, but I have a question about the chord progression.");
        comment2.setAnswerOn(comment1);

        Comment comment3 = new Comment();
        comment3.setText("Could you explain the part with the strumming pattern in more detail?");

        return List.of(comment1, comment2, comment3);
    }

    public static PersonalLibrary getPersonalLibrary() {
        return new PersonalLibrary();
    }

    public static SongBeat getSongBeat() {
        Chord chord = new Chord();
        chord.setName("Am");

        Fingering fingering = new Fingering();
        fingering.setChord(chord);
        fingering.setImg(new byte[]{1, 2, 3});

        SongBeat songBeat = new SongBeat();
        songBeat.setText("Beat 1");
        songBeat.setBeat(1);

        BeatChord beatChord = new BeatChord();
        beatChord.setSongBeat(songBeat);
        beatChord.setChord(chord);
        beatChord.setRecommendedFingering(fingering);

        songBeat.setBeatChords(Set.of(beatChord));
        fingering.setRecommendedFor(Set.of(beatChord));
        chord.setBeatChords(Set.of(beatChord));

        return songBeat;
    }
}
