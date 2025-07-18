package com.healthcare.healthcare_system.exception;

/**
 * The type Validation exception.
 */
public class ValidationException extends HealthcareSystemException {
    /**
     * Instantiates a new Validation exception.
     *
     * @param fieldName the field name
     * @param requirement the requirement
     * @param entityType the entity type
     */
    public ValidationException(String fieldName, String requirement, String entityType) {
        super("VALIDATION_ERROR",
                String.format("The %s field is invalid: %s", fieldName, requirement),
                String.format("Validation failed for %s in %s", fieldName, entityType),
                entityType);
    }
}