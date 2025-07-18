package com.healthcare.healthcare_system.repository;


import com.healthcare.healthcare_system.model.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    boolean existsByNameIgnoreCase(String name);

    Page<Facility> findAll(Pageable pageable);

    Page<Facility> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Facility> findByType(String type, Pageable pageable);

    Page<Facility> findByNameContainingIgnoreCaseAndType(String name, String type, Pageable pageable);

    @Query("SELECT f FROM Facility f WHERE SIZE(f.patients) > :count")
    List<Facility> findFacilitiesWithPatientCountGreaterThan(int count);

    @Query("SELECT f FROM Facility f WHERE SIZE(f.patients) > :count")
    List<Facility> findByPatientCountGreaterThan(@Param("count") int count);

}