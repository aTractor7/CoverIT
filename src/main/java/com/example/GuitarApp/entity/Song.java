package com.example.GuitarApp.entity;

import com.example.GuitarApp.entity.enums.SongGenre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Song implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 50, message = "Title cannot be longer than 50 characters")
    @Column(nullable = false, length = 50)
    private String title;

    @NotNull(message = "Genre is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private SongGenre genre;

    @NotNull(message = "Year of publish is required")
    @Column(name = "release_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate releaseDate;

    @ManyToMany(mappedBy = "songs", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Artist> songAuthors;

    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<SongTutorial> tutorials;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && Objects.equals(title, song.title) && genre == song.genre && Objects.equals(releaseDate, song.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, genre, releaseDate);
    }
}
