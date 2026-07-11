package com.complaint.model;

/**
 * Enum representing user access roles.
 */
public enum Role {
    USER("Standard User"),
    ADMIN("Administrator");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
