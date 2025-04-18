package com.example.GuitarApp.entity.enums;

import lombok.Getter;

@Getter
public enum Skill {
    BEGINNER(0, TutorialDifficulty.SIMPLIFIED),
    ADVANCED(1, TutorialDifficulty.ORIGINAL),;

    private final int level;
    private final TutorialDifficulty difficulty;

    Skill(int level, TutorialDifficulty difficulty) {
        this.level = level;
        this.difficulty = difficulty;
    }
}
