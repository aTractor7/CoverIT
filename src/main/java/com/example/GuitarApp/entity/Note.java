package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "notes",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "octave"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be null")
    @Size(max = 3, message = "Name must be less then 3 characters")
    @Column(nullable = false, length = 3)
    private String name;

    @NotNull(message = "Frequency cannot be null")
    @Column(nullable = false, unique = true, precision = 6)
    private Double frequency;

    @Column(nullable = false)
    private int octave;

    @OneToMany(mappedBy = "jamKey", fetch = FetchType.LAZY)
    private Set<Jam> jams;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id && octave == note.octave && Objects.equals(name, note.name) && Objects.equals(frequency, note.frequency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, frequency, octave);
    }
}
