package com.healthcare.healthcare_system.enums;

/**
 * The enum Facility type.
 */
public enum FacilityType {
    /**
     *Hospital facility type.
     */
    HOSPITAL("Hospital"),
    /**
     *Clinic facility type.
     */
    CLINIC("Clinic"),
    /**
     * The Diagnostic center.
     */
    DIAGNOSTIC_CENTER("Diagnostic Center"),
    /**
     *Pharmacy facility type.
     */
    PHARMACY("Pharmacy"),
    /**
     * The Nursing home.
     */
    NURSING_HOME("Nursing Home"),
    /**
     * The Rehabilitation center.
     */
    REHABILITATION_CENTER("Rehabilitation Center"),
    /**
     * The Mental health facility.
     */
    MENTAL_HEALTH_FACILITY("Mental Health Facility"),
    /**
     * The Urgent care.
     */
    URGENT_CARE("Urgent Care"),
    /**
     * The Specialty center.
     */
    SPECIALTY_CENTER("Specialty Center"),
    /**
     * The Dental clinic.
     */
    DENTAL_CLINIC("Dental Clinic"),
    /**
     * The Primary care.
     */
    PRIMARY_CARE("Primary Care Facility"),
    /**
     * The Surgical center.
     */
    SURGICAL_CENTER("Surgical Center"),
    /**
     * The Birthing center.
     */
    BIRTHING_CENTER("Birthing Center"),
    /**
     *Hospice facility type.
     */
    HOSPICE("Hospice"),
    /**
     * The Blood bank.
     */
    BLOOD_BANK("Blood Bank"),
    /**
     * The Medical laboratory.
     */
    MEDICAL_LABORATORY("Medical Laboratory"),
    /**
     * The Imaging center.
     */
    IMAGING_CENTER("Imaging Center"),
    /**
     * The Physical therapy center.
     */
    PHYSICAL_THERAPY_CENTER("Physical Therapy Center"),
    /**
     * The Home healthcare.
     */
    HOME_HEALTHCARE("Home Healthcare Service"),
    /**
     * The Telemedicine.
     */
    TELEMEDICINE("Telemedicine Provider");

    private final String displayName;

    FacilityType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}