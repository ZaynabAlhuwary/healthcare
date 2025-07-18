package com.healthcare.healthcare_system.controller;

import com.healthcare.healthcare_system.dto.FacilityDto;
import com.healthcare.healthcare_system.dto.PatientDto;
import com.healthcare.healthcare_system.service.FacilityService;
import com.healthcare.healthcare_system.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/facilities")
@RequiredArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;
    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<FacilityDto>> getAllFacilities(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(facilityService.getAllFacilities(pageable, name, type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacilityById(id));
    }

    @PostMapping
    public ResponseEntity<FacilityDto> createFacility(@Valid @RequestBody FacilityDto facilityDto) {
        return ResponseEntity.ok(facilityService.createFacility(facilityDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacilityDto> updateFacility(
            @PathVariable Long id, @Valid @RequestBody FacilityDto facilityDto) {
        return ResponseEntity.ok(facilityService.updateFacility(id, facilityDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{facilityId}/patients")
    public ResponseEntity<Page<PatientDto>> getPatientsByFacility(
            @PathVariable Long facilityId, Pageable pageable) {
        return ResponseEntity.ok(patientService.getPatientsByFacility(facilityId, pageable));
    }
}
