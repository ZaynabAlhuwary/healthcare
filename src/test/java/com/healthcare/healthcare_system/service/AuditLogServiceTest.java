package com.healthcare.healthcare_system.service;

import com.healthcare.healthcare_system.model.AuditLog;
import com.healthcare.healthcare_system.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog auditLog1;
    private AuditLog auditLog2;
    private List<AuditLog> auditLogs;

    @BeforeEach
    void setUp() {
        auditLog1 = AuditLog.builder()
                .entityType("Patient")
                .entityId(1L)
                .action("CREATE")
                .oldValue(null)
                .newValue("Patient details")
                .changedAt(LocalDateTime.now())
                .build();

        auditLog2 = AuditLog.builder()
                .entityType("Doctor")
                .entityId(2L)
                .action("UPDATE")
                .oldValue("Old details")
                .newValue("New details")
                .changedAt(LocalDateTime.now().minusHours(1))
                .build();

        auditLogs = Arrays.asList(auditLog1, auditLog2);
    }

    @Test
    void logAudit_ShouldSaveAuditLog() throws ExecutionException, InterruptedException {
        // Given
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog1);

        // When
        CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                auditLogService.logAudit("Patient", 1L, "CREATE", null, "Patient details")
        );
        future.get(); // Wait for async operation to complete

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void getAllAuditLogs_ShouldReturnPageOfAuditLogs() {
        // Given
        Pageable pageable = mock(Pageable.class);
        Page<AuditLog> page = new PageImpl<>(auditLogs);
        when(auditLogRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<AuditLog> result = auditLogService.getAllAuditLogs(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(auditLogRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAuditLogsByEntityType_ShouldReturnFilteredLogs() {
        // Given
        when(auditLogRepository.findByEntityTypeOrderByChangedAtDesc("Patient"))
                .thenReturn(Arrays.asList(auditLog1));

        // When
        List<AuditLog> result = auditLogService.getAuditLogsByEntityType("Patient");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Patient", result.get(0).getEntityType());
        verify(auditLogRepository, times(1))
                .findByEntityTypeOrderByChangedAtDesc("Patient");
    }

    @Test
    void getAuditLogsForEntity_ShouldReturnEntityLogs() {
        // Given
        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc("Patient", 1L))
                .thenReturn(Arrays.asList(auditLog1));

        // When
        List<AuditLog> result = auditLogService.getAuditLogsForEntity("Patient", 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEntityId());
        verify(auditLogRepository, times(1))
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc("Patient", 1L);
    }

    @Test
    void getAuditLogsByAction_ShouldReturnActionLogs() {
        // Given
        when(auditLogRepository.findByActionOrderByChangedAtDesc("CREATE"))
                .thenReturn(Arrays.asList(auditLog1));

        // When
        List<AuditLog> result = auditLogService.getAuditLogsByAction("CREATE");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
        verify(auditLogRepository, times(1))
                .findByActionOrderByChangedAtDesc("CREATE");
    }

    @Test
    void getAuditLogsByDateRange_ShouldReturnFilteredLogs() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        when(auditLogRepository.findByChangedAtBetweenOrderByChangedAtDesc(start, end))
                .thenReturn(auditLogs);

        // When
        List<AuditLog> result = auditLogService.getAuditLogsByDateRange(start, end);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(auditLogRepository, times(1))
                .findByChangedAtBetweenOrderByChangedAtDesc(start, end);
    }

    @Test
    void getEntityHistory_ShouldReturnEntityLogs() {
        // Given
        when(auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc("Patient", 1L))
                .thenReturn(Arrays.asList(auditLog1));

        // When
        List<AuditLog> result = auditLogService.getEntityHistory("Patient", 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Patient", result.get(0).getEntityType());
        assertEquals(1L, result.get(0).getEntityId());
        verify(auditLogRepository, times(1))
                .findByEntityTypeAndEntityIdOrderByChangedAtDesc("Patient", 1L);
    }
}