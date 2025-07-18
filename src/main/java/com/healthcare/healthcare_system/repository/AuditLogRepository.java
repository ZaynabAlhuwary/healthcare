package com.healthcare.healthcare_system.repository;

import com.healthcare.healthcare_system.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeOrderByChangedAtDesc(String entityType);

    List<AuditLog> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, Long entityId);

    List<AuditLog> findByActionOrderByChangedAtDesc(String action);

    List<AuditLog> findByChangedAtBetweenOrderByChangedAtDesc(LocalDateTime start, LocalDateTime end);
}