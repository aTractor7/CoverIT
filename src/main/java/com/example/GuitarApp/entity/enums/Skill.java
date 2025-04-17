package com.example.GuitarApp.entity.enums;

import lombok.Getter;

@Getter
public enum Skill {
    BEGINNER(0), ADVANCED(1);

    private final int level;

    Skill(int level) {
        this.level = level;
    }
}
