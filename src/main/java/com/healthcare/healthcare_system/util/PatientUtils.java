package com.healthcare.healthcare_system.util;


import com.healthcare.healthcare_system.dto.PatientDto;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.model.Patient;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

/**
 * The type Patient utils.
 */
public class PatientUtils {

    private PatientUtils() {
    }

    /**
     * Configure patient model mapper.
     *
     * @param modelMapper the model mapper
     */
    public static void configurePatientModelMapper(ModelMapper modelMapper) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.typeMap(Patient.class, PatientDto.class)
                .addMapping(src -> src.getFacility().getId(), PatientDto::setFacilityId);
    }

    /**
     * Map patient page to dto page.
     *
     * @param patients the patients
     * @param modelMapper the model mapper
     * @return the page
     */
    public static Page<PatientDto> mapPatientPageToDto(Page<Patient> patients, ModelMapper modelMapper) {
        return patients.map(patient -> modelMapper.map(patient, PatientDto.class));
    }

    /**
     * Map to entity patient.
     *
     * @param patientDto the patient dto
     * @param modelMapper the model mapper
     * @param facility the facility
     * @return the patient
     */
    public static Patient mapToEntity(PatientDto patientDto, ModelMapper modelMapper, Facility facility) {
        Patient patient = modelMapper.map(patientDto, Patient.class);
        patient.setFacility(facility);
        return patient;
    }
}
