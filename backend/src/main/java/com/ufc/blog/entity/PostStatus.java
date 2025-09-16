package com.ufc.blog.entity;

public enum PostStatus {
    DRAFT("Draft", "Rascunho - Post não publicado"),
    PUBLISHED("Published", "Publicado - Visível para todos"),
    ARCHIVED("Archived", "Arquivado - Não visível publicamente");

    private final String displayName;
    private final String description;

    PostStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublished() {
        return this == PUBLISHED;
    }

    public boolean isDraft() {
        return this == DRAFT;
    }

    public boolean isArchived() {
        return this == ARCHIVED;
    }

    public boolean isVisible() {
        return this == PUBLISHED;
    }

    @Override
    public String toString() {
        return displayName;
    }
}