package com.example.GuitarApp.entity.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String roleString;

    Role(String roleString) {
        this.roleString = roleString;
    }
}
