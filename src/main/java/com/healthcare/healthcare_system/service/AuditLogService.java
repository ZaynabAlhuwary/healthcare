package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.model.AuditLog;
import com.healthcare.healthcare_system.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Audit log service.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    /**
     * Log audit.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @param action the action
     * @param oldValue the old value
     * @param newValue the new value
     */
    @Async
    public void logAudit(String entityType, Long entityId, String action, String oldValue, String newValue) {
        AuditLog auditLog = AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        auditLogRepository.save(auditLog);
    }

    /**
     * Gets all audit logs.
     *
     * @param pageable the pageable
     * @return the all audit logs
     */
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    /**
     * Gets audit logs by entity type.
     *
     * @param entityType the entity type
     * @return the audit logs by entity type
     */
    public List<AuditLog> getAuditLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityTypeOrderByChangedAtDesc(entityType);
    }

    /**
     * Gets audit logs for entity.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the audit logs for entity
     */
    public List<AuditLog> getAuditLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId);
    }

    /**
     * Gets audit logs by action.
     *
     * @param action the action
     * @return the audit logs by action
     */
    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByChangedAtDesc(action);
    }

    /**
     * Gets audit logs by date range.
     *
     * @param start the start
     * @param end the end
     * @return the audit logs by date range
     */
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByChangedAtBetweenOrderByChangedAtDesc(start, end);
    }

    /**
     * Gets entity history.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the entity history
     */
    public List<AuditLog> getEntityHistory(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId);
    }
}