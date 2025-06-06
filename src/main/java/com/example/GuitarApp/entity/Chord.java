package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "chords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Chord implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    @OneToMany(mappedBy = "chord", fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Fingering> fingerings;

    @OneToMany(mappedBy = "chord", fetch = FetchType.LAZY)
    private Set<BeatChord> beatChords;

    public Chord(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Chord chord = (Chord) o;
        return id == chord.id && Objects.equals(name, chord.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
