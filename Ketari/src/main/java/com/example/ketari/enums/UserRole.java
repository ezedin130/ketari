package com.example.ketari.enums;

import java.util.Arrays;

/**
 * Platform user access roles.
 */
public enum UserRole {
    EMPLOYEE,
    EMPLOYER,
    ADMIN;

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            return EMPLOYEE;
        }
        String clean = value.trim().toUpperCase();
        if (clean.startsWith("ROLE_")) {
            clean = clean.substring(5);
        }
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(clean)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + value);
    }
}
