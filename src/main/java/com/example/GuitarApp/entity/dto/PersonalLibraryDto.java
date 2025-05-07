package com.example.GuitarApp.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PersonalLibraryDto {

    private int id;

    private LocalDateTime addDate;

    private UserDto owner;

    private SongTutorialShortDto songTutorial;
}
