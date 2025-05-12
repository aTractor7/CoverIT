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

    @Column(nullable = false, length = 50)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30)")
    private SongGenre genre;

    @Column(name = "release_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate releaseDate;

    @ManyToOne
    @JoinColumn(name = "created_by_user")
    private User createdBy;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "songs_authors",
            joinColumns = @JoinColumn(name = "song_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Artist> songAuthors;

    @OneToMany(mappedBy = "song", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<SongTutorial> tutorials;

    public Song(int id) {
        this.id = id;
    }

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
