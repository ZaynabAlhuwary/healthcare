package com.healthcare.healthcare_system.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HealthcareSystemException extends RuntimeException {
    private final String errorCode;
    private final String message;
    private final String technicalDetails;
    private final String entityType;

    public HealthcareSystemException(String errorCode, String message, String entityType) {
        this(errorCode, message, null, entityType);
    }
}