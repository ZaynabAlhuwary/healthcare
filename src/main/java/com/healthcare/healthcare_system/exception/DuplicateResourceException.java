package com.healthcare.healthcare_system.exception;

/**
 * The type Duplicate resource exception.
 */
public class DuplicateResourceException extends HealthcareSystemException {
    /**
     * Instantiates a new Duplicate resource exception.
     *
     * @param fieldName the field name
     * @param value the value
     * @param entityType the entity type
     */
    public DuplicateResourceException(String fieldName, String value, String entityType) {
        super("DUPLICATE_RESOURCE",
                String.format("A %s with this %s (%s) Please use a unique value.", entityType, fieldName, value),
                String.format("Duplicate %s found for %s: %s", entityType, fieldName, value),
                entityType);
    }
}