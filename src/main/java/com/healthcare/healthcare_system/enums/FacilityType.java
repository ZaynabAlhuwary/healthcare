package com.healthcare.healthcare_system.enums;

public enum FacilityType {
    HOSPITAL("Hospital"),
    CLINIC("Clinic"),
    DIAGNOSTIC_CENTER("Diagnostic Center"),
    PHARMACY("Pharmacy"),
    NURSING_HOME("Nursing Home"),
    REHABILITATION_CENTER("Rehabilitation Center"),
    MENTAL_HEALTH_FACILITY("Mental Health Facility"),
    URGENT_CARE("Urgent Care"),
    SPECIALTY_CENTER("Specialty Center"),
    DENTAL_CLINIC("Dental Clinic"),
    PRIMARY_CARE("Primary Care Facility"),
    SURGICAL_CENTER("Surgical Center"),
    BIRTHING_CENTER("Birthing Center"),
    HOSPICE("Hospice"),
    BLOOD_BANK("Blood Bank"),
    MEDICAL_LABORATORY("Medical Laboratory"),
    IMAGING_CENTER("Imaging Center"),
    PHYSICAL_THERAPY_CENTER("Physical Therapy Center"),
    HOME_HEALTHCARE("Home Healthcare Service"),
    TELEMEDICINE("Telemedicine Provider");

    private final String displayName;

    FacilityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}