package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "song_beats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"beat", "song_tutorial_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongBeat implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(max = 100, message = "Text must be up to 100 characters")
    private String text;

    @NotNull(message = "Beat must not be null")
    @Column(nullable = false, precision = 4)
    private Integer beat;

    @Column(length = 50)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_tutorial_id", referencedColumnName = "id", nullable = false)
    private SongTutorial songTutorial;

    @OneToMany(mappedBy = "songBeat", fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<BeatChord> beatChords;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SongBeat songBeat = (SongBeat) o;
        return id == songBeat.id && Objects.equals(text, songBeat.text) && Objects.equals(beat, songBeat.beat) && Objects.equals(songTutorial, songBeat.songTutorial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, beat, songTutorial);
    }
}
