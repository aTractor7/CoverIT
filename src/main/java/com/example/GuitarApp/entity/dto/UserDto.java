package com.example.GuitarApp.entity.dto;

import com.example.GuitarApp.entity.enums.Role;
import com.example.GuitarApp.entity.enums.Skill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;
    private String username;
    private String email;
    private byte[] profileImg;
    private LocalDate joinDate;
    private Role role;
    private Skill skill;
    private String instrument;
    private String bio;
}
