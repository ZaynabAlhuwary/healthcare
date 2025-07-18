package com.healthcare.healthcare_system.util;


import com.healthcare.healthcare_system.dto.PatientDto;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.model.Patient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

public class PatientUtils {

    private PatientUtils() {
    }

    public static void configurePatientModelMapper(ModelMapper modelMapper) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Patient.class, PatientDto.class)
                .addMapping(src -> src.getFacility().getId(), PatientDto::setFacilityId);
    }

    public static Page<PatientDto> mapPatientPageToDto(Page<Patient> patients, ModelMapper modelMapper) {
        return patients.map(patient -> modelMapper.map(patient, PatientDto.class));
    }

    public static Patient mapToEntity(PatientDto patientDto, ModelMapper modelMapper, Facility facility) {
        Patient patient = modelMapper.map(patientDto, Patient.class);
        patient.setFacility(facility);
        return patient;
    }
}
