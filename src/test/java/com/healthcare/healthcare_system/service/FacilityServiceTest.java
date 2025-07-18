package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.dto.FacilityDto;
import com.healthcare.healthcare_system.enums.FacilityType;
import com.healthcare.healthcare_system.exception.DuplicateResourceException;
import com.healthcare.healthcare_system.exception.ResourceNotFoundException;
import com.healthcare.healthcare_system.exception.ServiceException;
import com.healthcare.healthcare_system.exception.ValidationException;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.repository.FacilityRepository;
import com.healthcare.healthcare_system.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private FacilityService facilityService;

    private Facility facility;
    private FacilityDto facilityDto;
    private Page<Facility> facilityPage;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");
        facility.setType(FacilityType.HOSPITAL);
        facility.setAddress("123 Test St");
        facility.setDeleted(false);

        facilityDto = new FacilityDto();
        facilityDto.setId(1L);
        facilityDto.setName("Test Facility");
        facilityDto.setType(FacilityType.HOSPITAL.toString());
        facilityDto.setAddress("123 Test St");
        facilityDto.setPatientCount(0L);

        facilityPage = new PageImpl<>(Collections.singletonList(facility));
    }

    // GetAll Tests
    @Test
    void getAllFacilities_WithNoFilters_ShouldReturnAllFacilities() {
        when(facilityRepository.findAll(any(Pageable.class))).thenReturn(facilityPage);
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        Page<FacilityDto> result = facilityService.getAllFacilities(Pageable.unpaged(), null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(facilityRepository).findAll(any(Pageable.class));
        verify(modelMapper, times(1)).map(any(Facility.class), eq(FacilityDto.class));
    }

    @Test
    void getAllFacilities_WithNameFilter_ShouldReturnMatchingFacilities() {
        when(facilityRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(facilityPage);
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        Page<FacilityDto> result = facilityService.getAllFacilities(Pageable.unpaged(), "Test", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(facilityRepository).findByNameContainingIgnoreCase(eq("Test"), any(Pageable.class));
    }

    @Test
    void getAllFacilities_WithTypeFilter_ShouldReturnMatchingFacilities() {
        when(facilityRepository.findByType(anyString(), any(Pageable.class))).thenReturn(facilityPage);
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        Page<FacilityDto> result = facilityService.getAllFacilities(Pageable.unpaged(), null, "Hospital");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(facilityRepository).findByType(eq("Hospital"), any(Pageable.class));
    }

    @Test
    void getAllFacilities_WithNameAndTypeFilters_ShouldReturnMatchingFacilities() {
        when(facilityRepository.findByNameContainingIgnoreCaseAndType(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(facilityPage);
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        Page<FacilityDto> result = facilityService.getAllFacilities(Pageable.unpaged(), "Test", "Hospital");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(facilityRepository).findByNameContainingIgnoreCaseAndType(eq("Test"), eq("Hospital"), any(Pageable.class));
    }

    @Test
    void getAllFacilities_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.findAll(any(Pageable.class))).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(ServiceException.class, () ->
                facilityService.getAllFacilities(Pageable.unpaged(), null, null));
    }

    // GetById Tests
    @Test
    void getFacilityById_WhenFacilityExists_ShouldReturnFacilityDto() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(modelMapper.map(facility, FacilityDto.class)).thenReturn(facilityDto);

        FacilityDto result = facilityService.getFacilityById(1L);

        assertNotNull(result);
        assertEquals("Test Facility", result.getName());
        verify(facilityRepository).findById(1L);
    }

    @Test
    void getFacilityById_WhenFacilityDoesNotExist_ShouldThrowResourceNotFoundException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                facilityService.getFacilityById(1L));
    }

    @Test
    void getFacilityById_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.findById(1L)).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(ServiceException.class, () ->
                facilityService.getFacilityById(1L));
    }

    // Create Tests
    @Test
    void createFacility_WithValidData_ShouldReturnCreatedFacilityAndLogAudit() {
        when(facilityRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(modelMapper.map(facilityDto, Facility.class)).thenReturn(facility);
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);
        when(modelMapper.map(facility, FacilityDto.class)).thenReturn(facilityDto);

        FacilityDto result = facilityService.createFacility(facilityDto);

        assertNotNull(result);
        assertEquals("Test Facility", result.getName());
        verify(facilityRepository).save(any(Facility.class));
        verify(auditLogService).logAudit(anyString(), anyLong(), anyString(), isNull(), anyString());
    }

    @Test
    void createFacility_WithDuplicateName_ShouldThrowDuplicateResourceException() {
        when(facilityRepository.existsByNameIgnoreCase(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                facilityService.createFacility(facilityDto));
    }

    @Test
    void createFacility_WithMissingRequiredFields_ShouldThrowValidationException() {
        assertAll(
                () -> {
                    facilityDto.setName(null);
                    assertThrows(ValidationException.class, () -> facilityService.createFacility(facilityDto));
                    facilityDto.setName("Test Facility"); // reset
                },
                () -> {
                    facilityDto.setType(null);
                    assertThrows(ValidationException.class, () -> facilityService.createFacility(facilityDto));
                    facilityDto.setType("HOSPITAL"); // reset
                },
                () -> {
                    facilityDto.setAddress(null);
                    assertThrows(ValidationException.class, () -> facilityService.createFacility(facilityDto));
                }
        );
    }

    @Test
    void createFacility_WhenDatabaseConstraintViolation_ShouldThrowDuplicateResourceException() {
        when(facilityRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(modelMapper.map(facilityDto, Facility.class)).thenReturn(facility);
        when(facilityRepository.save(any(Facility.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(DuplicateResourceException.class, () ->
                facilityService.createFacility(facilityDto));
    }

    @Test
    void createFacility_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(modelMapper.map(facilityDto, Facility.class)).thenReturn(facility);
        when(facilityRepository.save(any(Facility.class))).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(ServiceException.class, () ->
                facilityService.createFacility(facilityDto));
    }

    @Test
    void updateFacility_WithValidData_ShouldReturnUpdatedFacilityAndLogAudit() {
        // Setup
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));

        doAnswer(invocation -> {
            FacilityDto source = invocation.getArgument(0);
            Facility destination = invocation.getArgument(1);
            destination.setName(source.getName());
            destination.setAddress(source.getAddress());
            return null;
        }).when(modelMapper).map(any(FacilityDto.class), any(Facility.class));

        // Second mapping: Facility -> FacilityDto (for return value)
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        when(facilityRepository.save(facility)).thenReturn(facility);

        // Execute
        FacilityDto result = facilityService.updateFacility(1L, facilityDto);

        // Verify
        assertNotNull(result);
        assertEquals("Test Facility", result.getName());
        verify(facilityRepository).save(facility);
        verify(auditLogService).logAudit(anyString(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void updateFacility_WhenFacilityNotFound_ShouldThrowResourceNotFoundException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                facilityService.updateFacility(1L, facilityDto));
    }

    @Test
    void updateFacility_WithMissingRequiredFields_ShouldThrowValidationException() {
        assertAll(
                () -> {
                    facilityDto.setName(null);
                    assertThrows(ValidationException.class, () -> facilityService.updateFacility(1L, facilityDto));
                    facilityDto.setName("Test Facility"); // reset
                },
                () -> {
                    facilityDto.setType(null);
                    assertThrows(ValidationException.class, () -> facilityService.updateFacility(1L, facilityDto));
                    facilityDto.setType("HOSPITAL"); // reset
                },
                () -> {
                    facilityDto.setAddress(null);
                    assertThrows(ValidationException.class, () -> facilityService.updateFacility(1L, facilityDto));
                }
        );
    }

    @Test
    void updateFacility_WhenDatabaseConstraintViolation_ShouldThrowDuplicateResourceException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(DuplicateResourceException.class, () ->
                facilityService.updateFacility(1L, facilityDto));
    }

    @Test
    void updateFacility_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(ServiceException.class, () ->
                facilityService.updateFacility(1L, facilityDto));
    }

    // Delete Tests
    @Test
    void deleteFacility_WhenFacilityExists_ShouldMarkAsDeletedAndLogAudit() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenReturn(facility);

        facilityService.deleteFacility(1L);

        assertTrue(facility.isDeleted());
        verify(facilityRepository).save(facility);
        verify(auditLogService).logAudit(anyString(), anyLong(), anyString(), anyString(), isNull());
    }

    @Test
    void deleteFacility_WhenFacilityNotFound_ShouldThrowResourceNotFoundException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                facilityService.deleteFacility(1L));
    }

    @Test
    void deleteFacility_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.save(any(Facility.class))).thenThrow(new DataAccessException("Database error") {
        });

        assertThrows(ServiceException.class, () ->
                facilityService.deleteFacility(1L));
    }

    // Special Query Tests
    @Test
    void getFacilitiesWithPatientCountGreaterThan_ShouldReturnFacilitiesWithHighPatientCount() {
        when(facilityRepository.findFacilitiesWithPatientCountGreaterThan(anyInt()))
                .thenReturn(Collections.singletonList(facility));
        when(modelMapper.map(any(Facility.class), eq(FacilityDto.class))).thenReturn(facilityDto);

        List<FacilityDto> result = facilityService.getFacilitiesWithPatientCountGreaterThan(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(facilityRepository).findFacilitiesWithPatientCountGreaterThan(5);
    }

    @Test
    void getFacilitiesWithPatientCountGreaterThan_WhenDatabaseErrorOccurs_ShouldThrowServiceException() {
        when(facilityRepository.findFacilitiesWithPatientCountGreaterThan(anyInt()))
                .thenThrow(new DataAccessException("Database error") {
                });

        assertThrows(ServiceException.class, () ->
                facilityService.getFacilitiesWithPatientCountGreaterThan(5));
    }
}