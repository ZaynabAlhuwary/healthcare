package com.healthcare.healthcare_system.exception;

/**
 * The type Resource not found exception.
 */
public class ResourceNotFoundException extends HealthcareSystemException {
    /**
     * Instantiates a new Resource not found exception.
     *
     * @param entityType the entity type
     */
    public ResourceNotFoundException(String entityType) {
        super("RESOURCE_NOT_FOUND",
                String.format("The requested %s was not found. It may have been removed or doesn't exist.", entityType),
                String.format("%s not found in the system", entityType),
                entityType);
    }

    /**
     * Instantiates a new Resource not found exception.
     *
     * @param entityType the entity type
     * @param id the id
     */
    public ResourceNotFoundException(String entityType, Long id) {
        super("RESOURCE_NOT_FOUND",
                String.format("The %s with ID %d was not found. Please check the ID and try again.", entityType, id),
                String.format("%s with ID %d not found in database", entityType, id),
                entityType);
    }
}