package org.equipment.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");

    private ValidationUtils() {
    }

    public static String normalizeName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is required.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2) {
            throw new IllegalArgumentException("Name must contain at least 2 letters.");
        }
        return trimmed;
    }

    public static String normalizePhone(String phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Phone is required.");
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Phone must follow ###-###-#### format.");
        }
        return trimmed;
    }

    public static void requirePositiveId(int id, String label) {
        if (id <= 0) {
            throw new IllegalArgumentException(label + " must be a positive number.");
        }
    }
}

