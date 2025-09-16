package com.ufc.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {
    ACTIVE("Active", "Usuário ativo"),
    INACTIVE("Inactive", "Usuário inativo"),
    SUSPENDED("Suspended", "Usuário suspenso"),
    PENDING("Pending", "Usuário pendente de ativação");

    private final String displayName;
    private final String description;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isInactive() {
        return this == INACTIVE;
    }

    public boolean isSuspended() {
        return this == SUSPENDED;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canPost() {
        return this == ACTIVE;
    }

    @Override
    public String toString() {
        return displayName;
    }
}