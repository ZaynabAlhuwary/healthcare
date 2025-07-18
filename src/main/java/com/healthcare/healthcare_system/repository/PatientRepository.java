package com.healthcare.healthcare_system.repository;

import com.healthcare.healthcare_system.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * The interface Patient repository.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findAll(Pageable pageable);

    /**
     * Find by facility id page.
     *
     * @param facilityId the facility id
     * @param pageable the pageable
     * @return the page
     */
    Page<Patient> findByFacilityId(Long facilityId, Pageable pageable);

    /**
     * Find by last name containing ignore case or first name containing ignore case page.
     *
     * @param lastName the last name
     * @param firstName the first name
     * @param pageable the pageable
     * @return the page
     */
    Page<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName, Pageable pageable);

    /**
     * Find by date of birth page.
     *
     * @param dateOfBirth the date of birth
     * @param pageable the pageable
     * @return the page
     */
    Page<Patient> findByDateOfBirth(LocalDate dateOfBirth, Pageable pageable);

    /**
     * Find by gender page.
     *
     * @param gender the gender
     * @param pageable the pageable
     * @return the page
     */
    Page<Patient> findByGender(String gender, Pageable pageable);

    /**
     * Count by facility id long.
     *
     * @param facilityId the facility id
     * @return the long
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.facility.id = :facilityId")
    long countByFacilityId(Long facilityId);

    /**
     * Exists by email and deleted false boolean.
     *
     * @param email the email
     * @return the boolean
     */
    boolean existsByEmailAndDeletedFalse(String email);

    /**
     * Exists by email and id not and deleted false boolean.
     *
     * @param email the email
     * @param id the id
     * @return the boolean
     */
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);

    /**
     * Exists by phone number and deleted false boolean.
     *
     * @param phoneNumber the phone number
     * @return the boolean
     */
    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);

    /**
     * Exists by phone number and id not and deleted false boolean.
     *
     * @param phoneNumber the phone number
     * @param id the id
     * @return the boolean
     */
    boolean existsByPhoneNumberAndIdNotAndDeletedFalse(String phoneNumber, Long id);

    /**
     * Exists by insurance number and deleted false boolean.
     *
     * @param insuranceNumber the insurance number
     * @return the boolean
     */
    boolean existsByInsuranceNumberAndDeletedFalse(String insuranceNumber);

    /**
     * Exists by insurance number and id not and deleted false boolean.
     *
     * @param insuranceNumber the insurance number
     * @param id the id
     * @return the boolean
     */
    boolean existsByInsuranceNumberAndIdNotAndDeletedFalse(String insuranceNumber, Long id);
}