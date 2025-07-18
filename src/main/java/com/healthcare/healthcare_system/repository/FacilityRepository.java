package com.healthcare.healthcare_system.repository;


import com.healthcare.healthcare_system.model.Facility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The interface Facility repository.
 */
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    /**
     * Exists by name ignore case boolean.
     *
     * @param name the name
     * @return the boolean
     */
    boolean existsByNameIgnoreCase(String name);

    Page<Facility> findAll(Pageable pageable);

    /**
     * Find by name containing ignore case page.
     *
     * @param name the name
     * @param pageable the pageable
     * @return the page
     */
    Page<Facility> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find by type page.
     *
     * @param type the type
     * @param pageable the pageable
     * @return the page
     */
    Page<Facility> findByType(String type, Pageable pageable);

    /**
     * Find by name containing ignore case and type page.
     *
     * @param name the name
     * @param type the type
     * @param pageable the pageable
     * @return the page
     */
    Page<Facility> findByNameContainingIgnoreCaseAndType(String name, String type, Pageable pageable);

    /**
     * Find facilities with patient count greater than list.
     *
     * @param count the count
     * @return the list
     */
    @Query("SELECT f FROM Facility f WHERE SIZE(f.patients) > :count")
    List<Facility> findFacilitiesWithPatientCountGreaterThan(int count);

    /**
     * Find by patient count greater than list.
     *
     * @param count the count
     * @return the list
     */
    @Query("SELECT f FROM Facility f WHERE SIZE(f.patients) > :count")
    List<Facility> findByPatientCountGreaterThan(@Param("count") int count);

}