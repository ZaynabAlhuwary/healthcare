package com.healthcare.healthcare_system.exception;

/**
 * The type Service exception.
 */
public class ServiceException extends HealthcareSystemException {
    /**
     * Instantiates a new Service exception.
     *
     * @param operation the operation
     * @param entityType the entity type
     * @param cause the cause
     */
    public ServiceException(String operation, String entityType, Throwable cause) {
        super("SERVICE_ERROR",
                String.format("We encountered an issue while processing your request for %s. Please try again later.", entityType),
                String.format("Failed to %s %s: %s", operation, entityType, cause.getMessage()),
                entityType);
    }
}