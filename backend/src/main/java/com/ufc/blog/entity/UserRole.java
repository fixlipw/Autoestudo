package com.ufc.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("User", "Usuário comum do sistema"),
    ADMIN("Admin", "Administrador do sistema");

    private final String displayName;
    private final String description;

    @Override
    public String toString() {
        return displayName;
    }
}