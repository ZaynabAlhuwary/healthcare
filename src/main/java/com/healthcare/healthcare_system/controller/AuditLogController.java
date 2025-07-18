package com.healthcare.healthcare_system.controller;

import com.healthcare.healthcare_system.model.AuditLog;
import com.healthcare.healthcare_system.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Audit log controller.
 */
@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * Instantiates a new Audit log controller.
     *
     * @param auditLogService the audit log service
     */
    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Gets all audit logs.
     *
     * @param page the page
     * @param size the size
     * @param sort the sort
     * @return the all audit logs
     */
    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "changedAt,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        Page<AuditLog> auditLogs = auditLogService.getAllAuditLogs(pageable);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Gets audit logs by entity type.
     *
     * @param entityType the entity type
     * @return the audit logs by entity type
     */
    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntityType(
            @PathVariable String entityType) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByEntityType(entityType);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Gets audit logs for entity.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the audit logs for entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsForEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsForEntity(entityType, entityId);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Gets audit logs by action.
     *
     * @param action the action
     * @return the audit logs by action
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(
            @PathVariable String action) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Gets audit logs by date range.
     *
     * @param start the start
     * @param end the end
     * @return the audit logs by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLog>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AuditLog> auditLogs = auditLogService.getAuditLogsByDateRange(start, end);
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * Gets entity history.
     *
     * @param entityType the entity type
     * @param entityId the entity id
     * @return the entity history
     */
    @GetMapping("/history/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLog>> getEntityHistory(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<AuditLog> history = auditLogService.getEntityHistory(entityType, entityId);
        return ResponseEntity.ok(history);
    }
}