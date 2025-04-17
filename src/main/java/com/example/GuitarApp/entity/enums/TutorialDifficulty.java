package com.example.GuitarApp.entity.enums;

import lombok.Getter;

@Getter
public enum TutorialDifficulty {
    SIMPLIFIED(0), ORIGINAL(1);

    private final int difficulty;

    TutorialDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
