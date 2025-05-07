package com.example.GuitarApp.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDto {

    @NotBlank(message = "Comment text cannot be empty")
    @Size(max = 1000, message = "Comment text cannot exceed 1000 characters")
    private String text;

    private int  idAnswerOn;

    @NotNull
    private Integer songTutorialId;
}
