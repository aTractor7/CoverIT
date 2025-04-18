package com.example.GuitarApp.entity.enums;

import lombok.Getter;

@Getter
public enum SongGenre {
    ROCK("Rock"),
    POP("Pop"),
    HIP_HOP("Hip Hop"),
    RAP("Rap"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    CLASSICAL("Classical"),
    COUNTRY("Country"),
    REGGAE("Reggae"),
    METAL("Metal"),
    PUNK("Punk"),
    SOUL("Soul"),
    RNB("R&B"),
    FOLK("Folk"),
    INDIE("Indie"),
    ALTERNATIVE("Alternative"),
    LATIN("Latin"),
    GOSPEL("Gospel"),
    SOUNDTRACK("Soundtrack"),
    ;

    private final String nameString;

    SongGenre(String nameString) {
        this.nameString = nameString;
    }
}
