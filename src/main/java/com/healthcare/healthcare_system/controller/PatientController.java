package com.healthcare.healthcare_system.controller;


import com.healthcare.healthcare_system.dto.PatientDto;
import com.healthcare.healthcare_system.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * The type Patient controller.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    /**
     * Gets all patients.
     *
     * @param pageable the pageable
     * @param search the search
     * @param dob the dob
     * @param gender the gender
     * @return the all patients
     */
    @GetMapping
    public ResponseEntity<Page<PatientDto>> getAllPatients(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate dob,
            @RequestParam(required = false) String gender) {
        return ResponseEntity.ok(patientService.getAllPatients(pageable, search, dob, gender));
    }

    /**
     * Gets patient by id.
     *
     * @param id the id
     * @return the patient by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    /**
     * Create patient response entity.
     *
     * @param patientDto the patient dto
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.createPatient(patientDto));
    }

    /**
     * Update patient response entity.
     *
     * @param id the id
     * @param patientDto the patient dto
     * @return the response entity
     */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable Long id, @Valid @RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDto));
    }

    /**
     * Delete patient response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}