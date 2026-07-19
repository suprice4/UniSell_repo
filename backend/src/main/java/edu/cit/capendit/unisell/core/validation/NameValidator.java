package edu.cit.capendit.unisell.core.validation;

public final class NameValidator {

    private NameValidator() {
    }

    public static void validateName(String name, String fieldLabel) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldLabel + " name cannot be empty");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException(fieldLabel + " name cannot exceed 100 characters");
        }
    }
}