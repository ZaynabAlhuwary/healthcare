package com.healthcare.healthcare_system.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The type Healthcare system exception.
 */
@Getter
@Setter
@AllArgsConstructor
public class HealthcareSystemException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private final String technicalDetails;
    private final String entityType;

    /**
     * Instantiates a new Healthcare system exception.
     *
     * @param errorCode the error code
     * @param message the message
     * @param entityType the entity type
     */
    public HealthcareSystemException(String errorCode, String message, String entityType) {
        this(errorCode, message, null, entityType);
    }
}