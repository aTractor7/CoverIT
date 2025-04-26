package com.example.GuitarApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Comment text cannot be empty")
    @Size(max = 1000, message = "Comment text cannot exceed 1000 characters")
    @Column(nullable = false, length = 1000)
    private String text;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answer_on", referencedColumnName = "id")
    private Comment answerOn;

    @OneToMany(mappedBy = "answerOn", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_tutorial_id", referencedColumnName = "id", nullable = false)
    private SongTutorial songTutorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id && Objects.equals(text, comment.text) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(comments, comment.comments) && Objects.equals(songTutorial, comment.songTutorial) && Objects.equals(user, comment.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, createdAt, comments, songTutorial, user);
    }
}
