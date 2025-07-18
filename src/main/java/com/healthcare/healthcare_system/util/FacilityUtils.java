package com.healthcare.healthcare_system.util;


import com.healthcare.healthcare_system.dto.FacilityDto;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.repository.PatientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Facility utils.
 */
public class FacilityUtils {

    private FacilityUtils() {
    }

    /**
     * Convert to dto facility dto.
     *
     * @param facility the facility
     * @param modelMapper the model mapper
     * @param patientRepository the patient repository
     * @return the facility dto
     */
    public static FacilityDto convertToDto(Facility facility, ModelMapper modelMapper, PatientRepository patientRepository) {
        FacilityDto dto = modelMapper.map(facility, FacilityDto.class);
        dto.setPatientCount(patientRepository.countByFacilityId(facility.getId()));
        return dto;
    }

    /**
     * Convert to dto page page.
     *
     * @param facilities the facilities
     * @param modelMapper the model mapper
     * @param patientRepository the patient repository
     * @return the page
     */
    public static Page<FacilityDto> convertToDtoPage(Page<Facility> facilities, ModelMapper modelMapper, PatientRepository patientRepository) {
        List<FacilityDto> dtos = facilities.getContent().stream()
                .map(facility -> convertToDto(facility, modelMapper, patientRepository))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, facilities.getPageable(), facilities.getTotalElements());
    }

    /**
     * Convert to dto list list.
     *
     * @param facilities the facilities
     * @param modelMapper the model mapper
     * @param patientRepository the patient repository
     * @return the list
     */
    public static List<FacilityDto> convertToDtoList(List<Facility> facilities, ModelMapper modelMapper, PatientRepository patientRepository) {
        return facilities.stream()
                .map(facility -> convertToDto(facility, modelMapper, patientRepository))
                .collect(Collectors.toList());
    }
}
