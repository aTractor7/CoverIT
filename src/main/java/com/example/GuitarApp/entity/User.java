package com.example.GuitarApp.entity;

import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Skill skill = Skill.BEGINNER;

    @Size(max = 30, message = "Instrument cannot be longer than 30 characters")
    private String instrument;

    @Size(max = 500, message = "Bio cannot be longer than 500 characters")
    private String bio;
}
