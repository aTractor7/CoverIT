package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_library",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "song_tutorial_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PersonalLibrary implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime addDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_tutorial_id", referencedColumnName = "id", nullable = false)
    private SongTutorial songTutorial;

    @PrePersist
    protected void onCreate() {
        this.addDate = LocalDateTime.now();
    }
}
