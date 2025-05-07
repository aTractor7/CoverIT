package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "fingerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fingering implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 200)
    private String imgPath;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chord_id", referencedColumnName = "id", nullable = false)
    private Chord chord;

    @OneToMany(mappedBy = "recommendedFingering", fetch = FetchType.LAZY)
    private Set<BeatChord> recommendedFor;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Fingering fingering = (Fingering) o;
        return id == fingering.id && Objects.equals(chord, fingering.chord) && Objects.equals(imgPath, fingering.imgPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chord, imgPath);
    }
}