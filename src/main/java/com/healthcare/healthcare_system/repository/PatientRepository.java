package com.healthcare.healthcare_system.repository;

import com.healthcare.healthcare_system.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findAll(Pageable pageable);

    Page<Patient> findByFacilityId(Long facilityId, Pageable pageable);

    Page<Patient> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName, Pageable pageable);

    Page<Patient> findByDateOfBirth(LocalDate dateOfBirth, Pageable pageable);

    Page<Patient> findByGender(String gender, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.facility.id = :facilityId")
    long countByFacilityId(Long facilityId);
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);
    boolean existsByPhoneNumberAndDeletedFalse(String phoneNumber);
    boolean existsByPhoneNumberAndIdNotAndDeletedFalse(String phoneNumber, Long id);
    boolean existsByInsuranceNumberAndDeletedFalse(String insuranceNumber);
    boolean existsByInsuranceNumberAndIdNotAndDeletedFalse(String insuranceNumber, Long id);
}