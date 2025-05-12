package com.example.GuitarApp.entity.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {

    private int id;

    private String text;

    private LocalDateTime createdAt;

    private UserDto author;

    private SongTutorialShortDto songTutorial;

    private int  idAnswerOn;
}
