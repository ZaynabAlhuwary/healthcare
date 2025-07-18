package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.dto.PatientDto;
import com.healthcare.healthcare_system.exception.DuplicateResourceException;
import com.healthcare.healthcare_system.exception.ResourceNotFoundException;
import com.healthcare.healthcare_system.exception.ServiceException;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.model.Patient;
import com.healthcare.healthcare_system.repository.FacilityRepository;
import com.healthcare.healthcare_system.repository.PatientRepository;
import com.healthcare.healthcare_system.util.PatientUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.healthcare.healthcare_system.util.MessageUtils.*;

/**
 * The type Patient service.
 */
@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final FacilityRepository facilityRepository;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    /**
     * Configure model mapper.
     */
    @PostConstruct
    public void configureModelMapper() {
        PatientUtils.configurePatientModelMapper(modelMapper);
    }

    /**
     * Gets all patients.
     *
     * @param pageable the pageable
     * @param search the search
     * @param dob the dob
     * @param gender the gender
     * @return the all patients
     */
    public Page<PatientDto> getAllPatients(Pageable pageable, String search, LocalDate dob, String gender) {
        try {
            Page<Patient> patients = findPatientsByCriteria(pageable, search, dob, gender);
            return PatientUtils.mapPatientPageToDto(patients, modelMapper);
        } catch (Exception e) {
            throw new ServiceException("retrieving patients", PATIENT_ENTITY_TYPE, e);
        }
    }

    /**
     * Gets patient by id.
     *
     * @param id the id
     * @return the patient by id
     */
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PATIENT_NOT_FOUND, id)));

        if (patient.isDeleted()) {
            throw new ResourceNotFoundException(
                    String.format(DELETED_PATIENT_ACCESS, id));
        }

        return modelMapper.map(patient, PatientDto.class);
    }

    /**
     * Gets patients by facility.
     *
     * @param facilityId the facility id
     * @param pageable the pageable
     * @return the patients by facility
     */
    public Page<PatientDto> getPatientsByFacility(Long facilityId, Pageable pageable) {
        try {
            validateFacilityExists(facilityId);
            Page<Patient> patients = patientRepository.findByFacilityId(facilityId, pageable);
            return PatientUtils.mapPatientPageToDto(patients, modelMapper);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Medical Facility",
                    facilityId);
        } catch (Exception e) {
            throw new ServiceException(
                    "retrieving facility patients",
                    PATIENT_ENTITY_TYPE,
                    e
            );
        }
    }

    /**
     * Create patient patient dto.
     *
     * @param patientDto the patient dto
     * @return the patient dto
     */
    @Transactional
    public PatientDto createPatient(PatientDto patientDto) {
        try {
            Facility facility = findFacilityByIdOrThrow(patientDto.getFacilityId());
            checkForDuplicatePatient(patientDto);

            Patient patient = PatientUtils.mapToEntity(patientDto, modelMapper, facility);
            Patient savedPatient = patientRepository.save(patient);

            auditLogService.logAudit(
                    PATIENT_ENTITY_TYPE,
                    savedPatient.getId(),
                    "CREATE",
                    null,
                    savedPatient.toString()
            );

            return modelMapper.map(savedPatient, PatientDto.class);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(
                    "Medical Facility",
                    patientDto.getFacilityId()
            );
        } catch (DuplicateResourceException e) {
            throw new DuplicateResourceException(
                    e.getMessage(),
                    PATIENT_ENTITY_TYPE,
                    "Please check for existing patients with similar details or contact support if you believe this is a mistake."
            );
        } catch (Exception e) {
            throw new ServiceException(
                    "creating patient",
                    PATIENT_ENTITY_TYPE,
                    e
            );
        }
    }

    /**
     * Update patient patient dto.
     *
     * @param id the id
     * @param patientDto the patient dto
     * @return the patient dto
     */
    @Transactional
    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        try {
            Patient existingPatient = findPatientByIdOrThrow(id);
            Facility facility = findFacilityByIdOrThrow(patientDto.getFacilityId());

            // Check for duplicates excluding the current patient
            checkForDuplicatePatient(patientDto, id);

            String oldValue = existingPatient.toString();
            modelMapper.map(patientDto, existingPatient);
            existingPatient.setFacility(facility);
            Patient updatedPatient = patientRepository.save(existingPatient);

            auditLogService.logAudit(
                    PATIENT_ENTITY_TYPE,
                    updatedPatient.getId(),
                    "UPDATE",
                    oldValue,
                    updatedPatient.toString()
            );

            return modelMapper.map(updatedPatient, PatientDto.class);
        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format(PATIENT_UPDATE_FAILED, id, e.getMessage()),
                    e
            );
        }
    }

    /**
     * Delete patient.
     *
     * @param id the id
     */
    @Transactional
    public void deletePatient(Long id) {
        try {
            Patient patient = findPatientByIdOrThrow(id);
            performSoftDelete(patient);
            auditLogService.logAudit(
                    PATIENT_ENTITY_TYPE,
                    patient.getId(),
                    "DELETE",
                    patient.toString(),
                    null
            );
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(PATIENT_DELETE_FAILED, id, e.getMessage()),
                    e
            );
        }
    }

    private void checkForDuplicatePatient(PatientDto patientDto) {
        checkForDuplicatePatient(patientDto, null);
    }

    private void checkForDuplicatePatient(PatientDto patientDto, Long excludePatientId) {
        List<String> conflictingFields = new ArrayList<>();

        // Check for duplicate email
        if (patientDto.getEmail() != null && !patientDto.getEmail().isEmpty()) {
            boolean emailExists = excludePatientId != null ?
                    patientRepository.existsByEmailAndIdNotAndDeletedFalse(patientDto.getEmail(), excludePatientId) :
                    patientRepository.existsByEmailAndDeletedFalse(patientDto.getEmail());
            if (emailExists) {
                conflictingFields.add("email address '" + patientDto.getEmail() + "'");
            }
        }

        // Check for duplicate phone number
        if (patientDto.getPhoneNumber() != null && !patientDto.getPhoneNumber().isEmpty()) {
            boolean phoneExists = excludePatientId != null ?
                    patientRepository.existsByPhoneNumberAndIdNotAndDeletedFalse(patientDto.getPhoneNumber(), excludePatientId) :
                    patientRepository.existsByPhoneNumberAndDeletedFalse(patientDto.getPhoneNumber());
            if (phoneExists) {
                conflictingFields.add("phone number '" + patientDto.getPhoneNumber() + "'");
            }
        }

        // Check for duplicate insurance number
        if (patientDto.getInsuranceNumber() != null && !patientDto.getInsuranceNumber().isEmpty()) {
            boolean insuranceNumberExists = excludePatientId != null ?
                    patientRepository.existsByInsuranceNumberAndIdNotAndDeletedFalse(patientDto.getInsuranceNumber(), excludePatientId) :
                    patientRepository.existsByInsuranceNumberAndDeletedFalse(patientDto.getInsuranceNumber());
            if (insuranceNumberExists) {
                conflictingFields.add("insurance number '" + patientDto.getInsuranceNumber() + "'");
            }
        }

        if (!conflictingFields.isEmpty()) {
            String fieldsList = String.join(", ", conflictingFields);
            if (conflictingFields.size() > 1) {
                // Replace the last comma with " and"
                int lastCommaIndex = fieldsList.lastIndexOf(",");
                if (lastCommaIndex != -1) {
                    fieldsList = fieldsList.substring(0, lastCommaIndex) + " and" + fieldsList.substring(lastCommaIndex + 1);
                }
            }
            throw new DuplicateResourceException("",
                    String.format("Another patient record exists with the same %s. %s",
                            fieldsList,
                            "Please verify the patient details or contact support if you need to merge records."),
                    PATIENT_ENTITY_TYPE);
        }
    }

    private Page<Patient> findPatientsByCriteria(Pageable pageable, String search, LocalDate dob, String gender) {
        try {
            if (search != null) {
                return patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
                        search, search, pageable);
            }
            if (dob != null) {
                return patientRepository.findByDateOfBirth(dob, pageable);
            }
            if (gender != null) {
                return patientRepository.findByGender(gender, pageable);
            }
            return patientRepository.findAll(pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search patients. Please check your search criteria.", e);
        }
    }

    private Patient findPatientByIdOrThrow(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(PATIENT_NOT_FOUND, id)));

        if (patient.isDeleted()) {
            throw new ResourceNotFoundException(
                    String.format(DELETED_PATIENT_ACCESS, id));
        }

        return patient;
    }

    private Facility findFacilityByIdOrThrow(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(FACILITY_NOT_FOUND, facilityId)));
    }

    private void validateFacilityExists(Long facilityId) {
        if (!facilityRepository.existsById(facilityId)) {
            throw new ResourceNotFoundException(
                    String.format(FACILITY_NOT_FOUND, facilityId));
        }
    }

    private void performSoftDelete(Patient patient) {
        try {
            patient.setDeleted(true);
            patientRepository.save(patient);
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Failed to soft delete patient with ID: %d", patient.getId()),
                    e
            );
        }
    }
}