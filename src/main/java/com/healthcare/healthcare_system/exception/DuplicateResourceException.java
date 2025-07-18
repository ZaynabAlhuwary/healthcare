package com.healthcare.healthcare_system.exception;

public class DuplicateResourceException extends HealthcareSystemException {
    public DuplicateResourceException(String fieldName, String value, String entityType) {
        super("DUPLICATE_RESOURCE",
                String.format("A %s with this %s (%s) Please use a unique value.", entityType, fieldName, value),
                String.format("Duplicate %s found for %s: %s", entityType, fieldName, value),
                entityType);
    }
}