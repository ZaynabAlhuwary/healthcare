package com.healthcare.healthcare_system.util;


public class MessageUtils {
    // Common messages
    public static final String FACILITY_ENTITY = "Facility";
    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";
    public static final String DELETE_ACTION = "DELETE";
    public static final String DATABASE_ERROR = "Failed due to database error";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String DUPLICATE_ENTRY = "Already exists with the same unique attributes";

    // Facility-specific messages
    public static final String FACILITY_NAME_REQUIRED = "Facility name is required";
    public static final String FACILITY_TYPE_REQUIRED = "Facility type is required";
    public static final String FACILITY_ADDRESS_REQUIRED = "Facility address is required";
    public static final String FACILITY_RETRIEVE_ERROR = "Failed to retrieve facility";
    public static final String FACILITIES_RETRIEVE_ERROR = "Failed to retrieve facilities";
    public static final String FACILITY_CREATE_ERROR = "Failed to create facility";
    public static final String FACILITY_UPDATE_ERROR = "Failed to update facility";
    public static final String FACILITY_DELETE_ERROR = "Failed to delete facility";
    public static final String FACILITY_SEARCH_ERROR = "Failed to search facilities";
    public static final String FACILITY_PATIENT_COUNT_ERROR = "Failed to retrieve facilities with patient count";

    //Patient
    public static final String PATIENT_ENTITY_TYPE = "Patient";
    public static final String PATIENT_NOT_FOUND = "We couldn't find a patient with ID %d. Please check the ID and try again. If you believe this is an error, please contact support.";
    public static final String FACILITY_NOT_FOUND = "The medical facility with ID %d doesn't exist in our system. Please verify the facility ID or contact your administrator for assistance.";
    public static final String DELETED_PATIENT_ACCESS = "The patient record with ID %d has been archived and is no longer accessible. If you need to restore this record, please contact the records department.";
    public static final String INVALID_PATIENT_DATA = "The patient information provided is incomplete or invalid: %s. Please review all required fields and try again.";
    public static final String DUPLICATE_PATIENT = "A patient with the same %s already exists in our system. To prevent duplicates, please verify the existing records or contact support if you believe this is an error.";
    public static final String PATIENT_CREATION_FAILED = "We couldn't create the new patient record due to: %s. Please review the information and try again. If the problem persists, contact technical support.";
    public static final String PATIENT_UPDATE_FAILED = "We couldn't update patient ID %d because: %s. Please verify your changes and try again. If you continue to experience issues, please contact support.";
    public static final String PATIENT_DELETE_FAILED = "We couldn't archive patient ID %d due to: %s. The record remains active. Please try again or contact system administration for assistance.";
    public static final String SEARCH_FAILED = "We encountered a problem while searching patient records. Please check your search criteria and try again. For complex searches, consider using more specific parameters.";

}
