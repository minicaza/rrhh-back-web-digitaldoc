package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentStatusRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentQueryServiceTest {

    @Mock private DocumentRepositoryPort documentRepositoryPort;
    @Mock private DocumentStatusRepositoryPort documentStatusRepositoryPort;
    @InjectMocks private DocumentQueryService service;

    // ── findByEmployee ────────────────────────────────────────────────────────

    @Test
    void findByEmployee_found_returnsViewWithLocalizedStatus() {
        var doc = pendingDocument();
        when(documentRepositoryPort.findByEmployeeIdAndManagedGroupId("1234567", "08"))
                .thenReturn(Optional.of(doc));
        when(documentStatusRepositoryPort.findStatusDescription(DocumentStatus.PENDING, "es-ES"))
                .thenReturn(Optional.of("Pendiente"));

        var result = service.findByEmployee("1234567", "08", "es-ES");

        assertThat(result).isPresent();
        assertThat(result.get().document()).isEqualTo(doc);
        assertThat(result.get().statusName()).isEqualTo("Pendiente");
    }

    @Test
    void findByEmployee_notFound_returnsEmpty() {
        when(documentRepositoryPort.findByEmployeeIdAndManagedGroupId(any(), any()))
                .thenReturn(Optional.empty());

        assertThat(service.findByEmployee("0000000", "99", "es-ES")).isEmpty();
        verifyNoInteractions(documentStatusRepositoryPort);
    }

    @Test
    void findByEmployee_localeNotFound_fallsBackToStatusName() {
        var doc = pendingDocument();
        when(documentRepositoryPort.findByEmployeeIdAndManagedGroupId("1234567", "08"))
                .thenReturn(Optional.of(doc));
        when(documentStatusRepositoryPort.findStatusDescription(DocumentStatus.PENDING, "fr-FR"))
                .thenReturn(Optional.empty());

        var result = service.findByEmployee("1234567", "08", "fr-FR");

        assertThat(result).isPresent();
        assertThat(result.get().statusName()).isEqualTo("PENDING");
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsView() {
        var doc = pendingDocument();
        when(documentRepositoryPort.findById(doc.getId())).thenReturn(Optional.of(doc));
        when(documentStatusRepositoryPort.findStatusDescription(DocumentStatus.PENDING, "es-ES"))
                .thenReturn(Optional.of("Pendiente"));

        var result = service.findById(doc.getId(), "es-ES");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(new DocumentView(doc, "Pendiente"));
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(documentRepositoryPort.findById(any())).thenReturn(Optional.empty());

        assertThat(service.findById(UUID.randomUUID(), "es-ES")).isEmpty();
    }

    // ── getDocumentStatus ─────────────────────────────────────────────────────

    @Test
    void getDocumentStatus_found_returnsStatusInfo() {
        var doc = pendingDocument();
        when(documentRepositoryPort.findById(doc.getId())).thenReturn(Optional.of(doc));
        when(documentStatusRepositoryPort.findStatusDescription(DocumentStatus.PENDING, "es-ES"))
                .thenReturn(Optional.of("Pendiente"));

        var result = service.getDocumentStatus(doc.getId(), "es-ES");

        assertThat(result).isPresent();
        assertThat(result.get().status()).isEqualTo(DocumentStatus.PENDING);
        assertThat(result.get().description()).isEqualTo("Pendiente");
    }

    @Test
    void getDocumentStatus_localeNotFound_fallsBackToStatusName() {
        var doc = pendingDocument();
        when(documentRepositoryPort.findById(doc.getId())).thenReturn(Optional.of(doc));
        when(documentStatusRepositoryPort.findStatusDescription(any(), any()))
                .thenReturn(Optional.empty());

        var result = service.getDocumentStatus(doc.getId(), "ja-JP");

        assertThat(result).isPresent();
        assertThat(result.get().description()).isEqualTo("PENDING");
    }

    @Test
    void getDocumentStatus_documentNotFound_returnsEmpty() {
        when(documentRepositoryPort.findById(any())).thenReturn(Optional.empty());

        assertThat(service.getDocumentStatus(UUID.randomUUID(), "es-ES")).isEmpty();
    }

    // ── listByStatus ─────────────────────────────────────────────────────────

    @Test
    void listByStatus_delegatesToRepositoryWithCorrectPageable() {
        var mockPage = mock(com.mercadona.framework.cna.commons.domain.MercadonaPage.class,
                RETURNS_DEEP_STUBS);
        when(documentRepositoryPort.findByStatus(eq(DocumentStatus.PENDING),
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10)))
                .thenReturn(mockPage);

        service.listByStatus(DocumentStatus.PENDING, "es-ES", 1, 10, "employeeId", "asc");

        verify(documentRepositoryPort).findByStatus(eq(DocumentStatus.PENDING), any());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Document pendingDocument() {
        return Document.builder()
                .id(UUID.randomUUID())
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.PENDING)
                .build();
    }
}
