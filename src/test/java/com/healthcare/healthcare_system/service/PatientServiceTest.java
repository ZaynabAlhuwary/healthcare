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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientDto patientDto;
    private Facility facility;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");

        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setEmail("john.doe@example.com");
        patient.setPhoneNumber("1234567890");
        patient.setFacility(facility);
        patient.setDeleted(false);

        patientDto = new PatientDto();
        patientDto.setId(1L);
        patientDto.setFirstName("John");
        patientDto.setLastName("Doe");
        patientDto.setEmail("john.doe@example.com");
        patientDto.setPhoneNumber("1234567890");
        patientDto.setFacilityId(1L);
    }

    @Test
    void getAllPatients_ShouldReturnPageOfPatients() {
        // Arrange
        Page<Patient> patientPage = new PageImpl<>(Collections.singletonList(patient));
        when(patientRepository.findAll(any(Pageable.class))).thenReturn(patientPage);
        when(modelMapper.map(any(Patient.class), eq(PatientDto.class))).thenReturn(patientDto);

        // Act
        Page<PatientDto> result = patientService.getAllPatients(Pageable.unpaged(), null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllPatients_WithSearchCriteria_ShouldReturnFilteredResults() {
        // Arrange
        Page<Patient> patientPage = new PageImpl<>(Collections.singletonList(patient));
        when(patientRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
                anyString(), anyString(), any(Pageable.class))).thenReturn(patientPage);
        when(modelMapper.map(any(Patient.class), eq(PatientDto.class))).thenReturn(patientDto);

        // Act
        Page<PatientDto> result = patientService.getAllPatients(Pageable.unpaged(), "Doe", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1))
                .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(anyString(), anyString(), any(Pageable.class));
    }

    @Test
    void getPatientById_ShouldReturnPatientWhenExists() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(modelMapper.map(patient, PatientDto.class)).thenReturn(patientDto);

        // Act
        PatientDto result = patientService.getPatientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_ShouldThrowExceptionWhenNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(1L));
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientById_ShouldThrowExceptionWhenPatientDeleted() {
        // Arrange
        patient.setDeleted(true);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientById(1L));
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void getPatientsByFacility_ShouldReturnPatientsForFacility() {
        // Arrange
        Page<Patient> patientPage = new PageImpl<>(Collections.singletonList(patient));
        when(facilityRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.findByFacilityId(1L, Pageable.unpaged())).thenReturn(patientPage);
        when(modelMapper.map(any(Patient.class), eq(PatientDto.class))).thenReturn(patientDto);

        // Act
        Page<PatientDto> result = patientService.getPatientsByFacility(1L, Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(patientRepository, times(1)).findByFacilityId(1L, Pageable.unpaged());
    }

    @Test
    void getPatientsByFacility_ShouldThrowExceptionWhenFacilityNotFound() {
        // Arrange
        when(facilityRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatientsByFacility(1L, Pageable.unpaged()));
        verify(facilityRepository, times(1)).existsById(1L);
    }

    @Test
    void createPatient_ShouldSuccessfullyCreatePatient() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(patientRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumberAndDeletedFalse(anyString())).thenReturn(false);

        when(modelMapper.map(patientDto, Patient.class)).thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(modelMapper.map(patient, PatientDto.class)).thenReturn(patientDto);

        PatientDto result = patientService.createPatient(patientDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(auditLogService, times(1)).logAudit(anyString(), anyLong(), anyString(), any(), any());
    }

    @Test
    void createPatient_ShouldThrowExceptionWhenFacilityNotFound() {
        // Arrange
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.createPatient(patientDto));
        verify(facilityRepository, times(1)).findById(1L);
    }

    @Test
    void createPatient_ShouldThrowExceptionWhenDuplicateEmail() {
        // Arrange
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(patientRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(patientDto));
        verify(patientRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
    }

    @Test
    void updatePatient_ShouldSuccessfullyUpdatePatient() {
        // Arrange
        PatientDto updatedDto = new PatientDto();
        updatedDto.setId(1L);
        updatedDto.setFirstName("Updated");
        updatedDto.setLastName("Doe");
        updatedDto.setEmail("john.doe@example.com");
        updatedDto.setPhoneNumber("1234567890");
        updatedDto.setFacilityId(1L);

        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setFirstName("Updated");
        updatedPatient.setLastName("Doe");
        updatedPatient.setEmail("john.doe@example.com");
        updatedPatient.setPhoneNumber("1234567890");
        updatedPatient.setFacility(facility);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        // Only mock the duplicate checks if they're actually needed for this test
        // In this case, since we're not changing the email/phone/insurance, they might not be needed
        lenient().when(patientRepository.existsByEmailAndIdNotAndDeletedFalse(anyString(), anyLong())).thenReturn(false);
        lenient().when(patientRepository.existsByPhoneNumberAndIdNotAndDeletedFalse(anyString(), anyLong())).thenReturn(false);
        lenient().when(patientRepository.existsByInsuranceNumberAndIdNotAndDeletedFalse(anyString(), anyLong())).thenReturn(false);

        // Setup the mapping behavior
        doAnswer(invocation -> {
            PatientDto source = invocation.getArgument(0);
            Patient destination = invocation.getArgument(1);
            destination.setFirstName(source.getFirstName());
            destination.setLastName(source.getLastName());
            destination.setFacility(facility);
            return null;
        }).when(modelMapper).map(updatedDto, patient);

        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        when(modelMapper.map(updatedPatient, PatientDto.class)).thenReturn(updatedDto);

        // Act
        PatientDto result = patientService.updatePatient(1L, updatedDto);

        // Assert
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(auditLogService, times(1)).logAudit(anyString(), anyLong(), anyString(), any(), any());
    }

    @Test
    void updatePatient_ShouldThrowExceptionWhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatient(1L, patientDto));
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void updatePatient_ShouldThrowExceptionWhenFacilityNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.updatePatient(1L, patientDto));
        verify(facilityRepository, times(1)).findById(1L);
    }

    @Test
    void deletePatient_ShouldSoftDeletePatient() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // Act
        patientService.deletePatient(1L);

        // Assert
        assertTrue(patient.isDeleted());
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(auditLogService, times(1)).logAudit(anyString(), anyLong(), anyString(), any(), any());
    }

    @Test
    void deletePatient_ShouldThrowExceptionWhenPatientNotFound() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.deletePatient(1L));
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void checkForDuplicatePatient_ShouldThrowExceptionForDuplicateEmail() {
        // Arrange
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(patientRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> patientService.createPatient(patientDto));

        verify(patientRepository, times(1)).existsByEmailAndDeletedFalse(anyString());
    }

    @Test
    void checkForDuplicatePatient_ShouldThrowExceptionForDuplicatePhoneNumber() {
        // Arrange
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(patientRepository.existsByEmailAndDeletedFalse(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumberAndDeletedFalse(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> patientService.createPatient(patientDto));
    }

    @Test
    void findPatientsByCriteria_ShouldHandleServiceException() {
        // Arrange
        when(patientRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(ServiceException.class,
                () -> patientService.getAllPatients(Pageable.unpaged(), null, null, null));
    }
}