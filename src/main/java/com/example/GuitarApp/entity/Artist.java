package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name cannot be longer than 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    @Column(length = 500)
    private String bio;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "songs_authors",
            joinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "song_id", referencedColumnName = "id", nullable = false)
    )
    private Set<Song> songs;
}
