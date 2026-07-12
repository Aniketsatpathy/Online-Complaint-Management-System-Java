package com.complaint.model;

/**
 * Enum representing the classification category of a complaint.
 */
public enum ComplaintCategory {
    TECHNICAL("Technical"),
    BILLING("Billing"),
    SERVICE("Service"),
    PRODUCT("Product"),
    INFRASTRUCTURE("Infrastructure"),
    OTHER("Other");

    private final String displayName;

    ComplaintCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
