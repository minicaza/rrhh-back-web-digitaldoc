package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentErrorMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.DocumentMOMapper;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentErrorMO;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentMO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentRepositoryAdapterTest {

    @Mock DocumentMOJpaRepository jpaRepository;
    @Mock DocumentErrorMOJpaRepository errorJpaRepository;
    @Mock DocumentMOMapper mapper;
    @InjectMocks DocumentRepositoryAdapter adapter;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    void save_delegatesToJpaRepositoryAndMapper() {
        Document domain = document(DocumentStatus.PENDING);
        DocumentMO mo = documentMO(DocumentStatus.PENDING);
        when(mapper.toMO(domain)).thenReturn(mo);
        when(jpaRepository.save(mo)).thenReturn(mo);
        when(mapper.toDomain(mo)).thenReturn(domain);

        Document result = adapter.save(domain);

        assertThat(result).isEqualTo(domain);
        verify(jpaRepository).save(mo);
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_returnsDocument_whenFound() {
        DocumentMO mo = documentMO(DocumentStatus.PENDING);
        Document domain = document(DocumentStatus.PENDING);
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(mo));
        when(mapper.toDomain(mo)).thenReturn(domain);

        Optional<Document> result = adapter.findById(DOCUMENT_ID);

        assertThat(result).contains(domain);
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThat(adapter.findById(DOCUMENT_ID)).isEmpty();
    }

    // ── findByEmployeeIdAndManagedGroupId ─────────────────────────────────────

    @Test
    void findByEmployeeIdAndManagedGroupId_returnsDocument_whenFound() {
        DocumentMO mo = documentMO(DocumentStatus.ENRICHED);
        Document domain = document(DocumentStatus.ENRICHED);
        when(jpaRepository.findByEmployeeIdAndManagedGroupId("E001", "08")).thenReturn(Optional.of(mo));
        when(mapper.toDomain(mo)).thenReturn(domain);

        assertThat(adapter.findByEmployeeIdAndManagedGroupId("E001", "08")).contains(domain);
    }

    @Test
    void findByEmployeeIdAndManagedGroupId_returnsEmpty_whenNotFound() {
        when(jpaRepository.findByEmployeeIdAndManagedGroupId("E001", "08")).thenReturn(Optional.empty());

        assertThat(adapter.findByEmployeeIdAndManagedGroupId("E001", "08")).isEmpty();
    }

    // ── findAllById ───────────────────────────────────────────────────────────

    @Test
    void findAllById_returnsMappedList() {
        List<UUID> ids = List.of(DOCUMENT_ID);
        DocumentMO mo = documentMO(DocumentStatus.PENDING);
        Document domain = document(DocumentStatus.PENDING);
        when(jpaRepository.findAllById(ids)).thenReturn(List.of(mo));
        when(mapper.toDomainList(List.of(mo))).thenReturn(List.of(domain));

        assertThat(adapter.findAllById(ids)).containsExactly(domain);
    }

    // ── updateStatus ──────────────────────────────────────────────────────────

    @Test
    void updateStatus_setsNewStatusAndSaves_whenDocumentExists() {
        DocumentMO mo = documentMO(DocumentStatus.PENDING);
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(mo));

        adapter.updateStatus(DOCUMENT_ID, DocumentStatus.ENRICHED);

        assertThat(mo.getDocumentStatusId()).isEqualTo(DocumentStatus.ENRICHED.getId());
        verify(jpaRepository).save(mo);
    }

    @Test
    void updateStatus_doesNothing_whenDocumentNotFound() {
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        adapter.updateStatus(DOCUMENT_ID, DocumentStatus.ENRICHED);

        verify(jpaRepository, never()).save(any());
    }

    // ── markFailed ────────────────────────────────────────────────────────────

    @Test
    void markFailed_setsFailedStatusAndSavesErrorRecord() {
        DocumentMO mo = documentMO(DocumentStatus.PENDING);
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(mo));

        adapter.markFailed(DOCUMENT_ID, FailedStep.ENRICHMENT, "timeout");

        assertThat(mo.getDocumentStatusId()).isEqualTo(DocumentStatus.FAILED.getId());
        verify(jpaRepository).save(mo);

        ArgumentCaptor<DocumentErrorMO> errorCaptor = ArgumentCaptor.forClass(DocumentErrorMO.class);
        verify(errorJpaRepository).save(errorCaptor.capture());
        DocumentErrorMO savedError = errorCaptor.getValue();
        assertThat(savedError.getDocumentId()).isEqualTo(DOCUMENT_ID);
        assertThat(savedError.getFailedStep()).isEqualTo("ENRICHMENT");
        assertThat(savedError.getErrorMessage()).isEqualTo("timeout");
    }

    @Test
    void markFailed_stillSavesErrorRecord_whenDocumentNotFound() {
        when(jpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        adapter.markFailed(DOCUMENT_ID, FailedStep.PDF_GENERATION, "oom");

        verify(jpaRepository, never()).save(any());
        verify(errorJpaRepository).save(any(DocumentErrorMO.class));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private DocumentMO documentMO(DocumentStatus status) {
        return DocumentMO.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .documentStatusId(status.getId())
                .build();
    }

    private Document document(DocumentStatus status) {
        return Document.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .status(status)
                .build();
    }
}
