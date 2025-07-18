package com.healthcare.healthcare_system.repository;

import com.healthcare.healthcare_system.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface Audit log repository.
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    /**
     * Find by entity type order by changed at desc list.
     *
     * @param entityType the entity type
     * @return the list
     */
    List<AuditLog> findByEntityTypeOrderByChangedAtDesc(String entityType);

    /**
     * Find by entity type and entity id order by changed at desc list.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the list
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, Long entityId);

    /**
     * Find by action order by changed at desc list.
     *
     * @param action the action
     * @return the list
     */
    List<AuditLog> findByActionOrderByChangedAtDesc(String action);

    /**
     * Find by changed at between order by changed at desc list.
     *
     * @param start the start
     * @param end the end
     * @return the list
     */
    List<AuditLog> findByChangedAtBetweenOrderByChangedAtDesc(LocalDateTime start, LocalDateTime end);
}