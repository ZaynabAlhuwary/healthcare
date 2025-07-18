package com.healthcare.healthcare_system.exception;

public class ValidationException extends HealthcareSystemException {
    public ValidationException(String fieldName, String requirement, String entityType) {
        super("VALIDATION_ERROR",
                String.format("The %s field is invalid: %s", fieldName, requirement),
                String.format("Validation failed for %s in %s", fieldName, entityType),
                entityType);
    }
}