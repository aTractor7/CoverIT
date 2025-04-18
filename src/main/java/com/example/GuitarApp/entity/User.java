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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 30, message = "Username cannot be longer than 30 characters")
    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Size(max = 50, message = "Email cannot be longer than 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    //TODO: add custom annotation validation on password
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 100, message = "Password should be at least 8 characters. Or less then 100")
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

    @NotNull(message = "Skill cannot be null")
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Skill skill = Skill.BEGINNER;

    @Size(max = 30, message = "Instrument cannot be longer than 30 characters")
    private String instrument;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;

    @OneToMany(mappedBy = "tutorialAuthor", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<SongTutorial> tutorials;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Comment> comments;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<PersonalLibrary> library;

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
