package com.example.GuitarApp.entity;

import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Lob
    private byte[] profileImg;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.USER;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Skill skill = Skill.BEGINNER;

    @Column(length = 30)
    private String instrument;

    @Column(length = 500)
    private String bio;

    @OneToMany(mappedBy = "tutorialAuthor", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<SongTutorial> tutorials;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Comment> comments;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PersonalLibrary> library;

    public User(int id) {
        this.id = id;
    }

    @PrePersist
    protected void onCreate() {
        this.joinDate = LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.deepEquals(profileImg, user.profileImg) && Objects.equals(joinDate, user.joinDate) && role == user.role && skill == user.skill && Objects.equals(instrument, user.instrument) && Objects.equals(bio, user.bio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password, Arrays.hashCode(profileImg), joinDate, role, skill, instrument, bio);
    }
}
