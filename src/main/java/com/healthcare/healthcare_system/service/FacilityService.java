package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.dto.FacilityDto;
import com.healthcare.healthcare_system.exception.DuplicateResourceException;
import com.healthcare.healthcare_system.exception.ResourceNotFoundException;
import com.healthcare.healthcare_system.exception.ServiceException;
import com.healthcare.healthcare_system.exception.ValidationException;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.repository.FacilityRepository;
import com.healthcare.healthcare_system.repository.PatientRepository;
import com.healthcare.healthcare_system.util.FacilityUtils;
import com.healthcare.healthcare_system.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.healthcare.healthcare_system.util.FacilityUtils.convertToDto;
import static com.healthcare.healthcare_system.util.FacilityUtils.convertToDtoPage;
import static com.healthcare.healthcare_system.util.MessageUtils.*;

@Service
@RequiredArgsConstructor
public class FacilityService {


    private final FacilityRepository facilityRepository;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    public Page<FacilityDto> getAllFacilities(Pageable pageable, String name, String type) {
        try {
            Page<Facility> facilities = findFacilitiesByCriteria(pageable, name, type);
            return convertToDtoPage(facilities, modelMapper, patientRepository);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITIES_RETRIEVE_ERROR, FACILITY_ENTITY, e);
        }
    }

    public FacilityDto getFacilityById(Long id) {
        try {
            Facility facility = findFacilityByIdOrThrow(id);
            return convertToDto(facility, modelMapper, patientRepository);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_RETRIEVE_ERROR, FACILITY_ENTITY, e);
        }
    }

    @Transactional
    public FacilityDto createFacility(FacilityDto facilityDto) {
        try {
            validateFacilityDto(facilityDto);
            checkForDuplicateFacility(facilityDto);

            Facility facility = modelMapper.map(facilityDto, Facility.class);
            Facility savedFacility = facilityRepository.save(facility);

            auditLogService.logAudit(FACILITY_ENTITY, savedFacility.getId(),
                    CREATE_ACTION, null, savedFacility.toString());
            return convertToDto(savedFacility, modelMapper, patientRepository);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(facilityDto.getName(), MessageUtils.DUPLICATE_ENTRY, FACILITY_ENTITY);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_CREATE_ERROR, FACILITY_ENTITY, e);
        }
    }

    @Transactional
    public FacilityDto updateFacility(Long id, FacilityDto facilityDto) {
        try {
            validateFacilityDto(facilityDto);
            Facility existingFacility = findFacilityByIdOrThrow(id);
            String oldValue = existingFacility.toString();

            modelMapper.map(facilityDto, existingFacility);
            Facility updatedFacility = facilityRepository.save(existingFacility);

            auditLogService.logAudit(FACILITY_ENTITY, updatedFacility.getId(),
                    UPDATE_ACTION, oldValue, updatedFacility.toString());
            return convertToDto(updatedFacility, modelMapper, patientRepository);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException(facilityDto.getName(), MessageUtils.DUPLICATE_ENTRY, FACILITY_ENTITY);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_UPDATE_ERROR, FACILITY_ENTITY, e);
        }
    }

    @Transactional
    public void deleteFacility(Long id) {
        try {
            Facility facility = findFacilityByIdOrThrow(id);
            facility.setDeleted(true);
            facilityRepository.save(facility);

            auditLogService.logAudit(FACILITY_ENTITY, facility.getId(),
                    DELETE_ACTION, facility.toString(), null);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_DELETE_ERROR, FACILITY_ENTITY, e);
        }
    }

    public List<FacilityDto> getFacilitiesWithPatientCountGreaterThan(int count) {
        try {
            List<Facility> facilities = facilityRepository.findFacilitiesWithPatientCountGreaterThan(count);
            List<FacilityDto> facilityDtos = FacilityUtils.convertToDtoList(facilities, modelMapper, patientRepository);
            return facilityDtos;
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_PATIENT_COUNT_ERROR, FACILITY_ENTITY, e);
        }
    }

    private Facility findFacilityByIdOrThrow(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(FACILITY_ENTITY));
    }

    private Page<Facility> findFacilitiesByCriteria(Pageable pageable, String name, String type) {
        try {
            if (name != null && type != null) {
                return facilityRepository.findByNameContainingIgnoreCaseAndType(name, type, pageable);
            } else if (name != null) {
                return facilityRepository.findByNameContainingIgnoreCase(name, pageable);
            } else if (type != null) {
                return facilityRepository.findByType(type, pageable);
            }
            return facilityRepository.findAll(pageable);
        } catch (DataAccessException e) {
            throw new ServiceException(MessageUtils.FACILITY_SEARCH_ERROR, FACILITY_ENTITY, e);
        }
    }

    private void validateFacilityDto(FacilityDto facilityDto) {
        if (facilityDto.getName() == null || facilityDto.getName().trim().isEmpty()) {
            throw new ValidationException("name", MessageUtils.FACILITY_NAME_REQUIRED, FACILITY_ENTITY);
        }
        if (facilityDto.getType() == null || facilityDto.getType().trim().isEmpty()) {
            throw new ValidationException("type", MessageUtils.FACILITY_TYPE_REQUIRED, FACILITY_ENTITY);
        }
        if (facilityDto.getAddress() == null || facilityDto.getAddress().trim().isEmpty()) {
            throw new ValidationException("address", FACILITY_ADDRESS_REQUIRED, FACILITY_ENTITY);
        }
    }

    private void checkForDuplicateFacility(FacilityDto facilityDto) {
        boolean exists = facilityRepository.existsByNameIgnoreCase(facilityDto.getName());
        if (exists) {
            throw new DuplicateResourceException(facilityDto.getName(), MessageUtils.DUPLICATE_ENTRY, FACILITY_ENTITY);
        }
    }


}