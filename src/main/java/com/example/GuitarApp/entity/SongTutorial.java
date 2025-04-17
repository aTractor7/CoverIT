package com.example.GuitarApp.entity;

import com.example.GuitarApp.entity.enums.TutorialDifficulty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "song_tutorials", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongTutorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Difficulty cannot be null")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private TutorialDifficulty difficulty;

    @Size(max = 100, message = "Description cannot be longer than 100 characters")
    @Column(length = 100)
    private String description;

    @Lob
    private byte[] backtrack;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Size(max = 20, message = "Recommended strumming cannot be longer than 20 characters")
    @Column(length = 20)
    private String recommendedStrumming;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User tutorialAuthor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id", referencedColumnName = "id", nullable = false)
    private Song song;

    @OneToMany(mappedBy = "songTutorial", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Comment> comments;
}
