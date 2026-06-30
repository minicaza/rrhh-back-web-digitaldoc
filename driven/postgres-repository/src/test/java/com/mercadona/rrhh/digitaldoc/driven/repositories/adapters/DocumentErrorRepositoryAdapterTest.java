package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.rrhh.digitaldoc.domain.DocumentError;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentErrorMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.DocumentErrorMOMapper;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentErrorMO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentErrorRepositoryAdapterTest {

    @Mock DocumentErrorMOJpaRepository jpaRepository;
    @Mock DocumentErrorMOMapper mapper;
    @InjectMocks DocumentErrorRepositoryAdapter adapter;

    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    // ── save ──────────────────────────────────────────────────────────────────

    @Test
    void save_persistsAndReturnsMappedDomain() {
        DocumentError domain = documentError();
        DocumentErrorMO mo = documentErrorMO();
        when(mapper.toMO(domain)).thenReturn(mo);
        when(jpaRepository.save(mo)).thenReturn(mo);
        when(mapper.toDomain(mo)).thenReturn(domain);

        DocumentError result = adapter.save(domain);

        assertThat(result).isEqualTo(domain);
        verify(jpaRepository).save(mo);
    }

    // ── findByDocumentId ──────────────────────────────────────────────────────

    @Test
    void findByDocumentId_returnsMappedList() {
        DocumentErrorMO mo = documentErrorMO();
        DocumentError domain = documentError();
        when(jpaRepository.findByDocumentId(DOCUMENT_ID)).thenReturn(List.of(mo));
        when(mapper.toDomainList(List.of(mo))).thenReturn(List.of(domain));

        assertThat(adapter.findByDocumentId(DOCUMENT_ID)).containsExactly(domain);
    }

    @Test
    void findByDocumentId_returnsEmptyList_whenNoneFound() {
        when(jpaRepository.findByDocumentId(DOCUMENT_ID)).thenReturn(List.of());
        when(mapper.toDomainList(List.of())).thenReturn(List.of());

        assertThat(adapter.findByDocumentId(DOCUMENT_ID)).isEmpty();
    }

    // ── findLatestByDocumentId ────────────────────────────────────────────────

    @Test
    void findLatestByDocumentId_returnsMappedError_whenFound() {
        DocumentErrorMO mo = documentErrorMO();
        DocumentError domain = documentError();
        when(jpaRepository.findTopByDocumentIdOrderByErrorTimeDesc(DOCUMENT_ID)).thenReturn(Optional.of(mo));
        when(mapper.toDomain(mo)).thenReturn(domain);

        assertThat(adapter.findLatestByDocumentId(DOCUMENT_ID)).contains(domain);
    }

    @Test
    void findLatestByDocumentId_returnsEmpty_whenNoneFound() {
        when(jpaRepository.findTopByDocumentIdOrderByErrorTimeDesc(DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThat(adapter.findLatestByDocumentId(DOCUMENT_ID)).isEmpty();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private DocumentErrorMO documentErrorMO() {
        return DocumentErrorMO.builder()
                .id(1L)
                .documentId(DOCUMENT_ID)
                .failedStep("ENRICHMENT")
                .errorMessage("timeout")
                .build();
    }

    private DocumentError documentError() {
        return DocumentError.builder()
                .id(1L)
                .documentId(DOCUMENT_ID)
                .failedStep("ENRICHMENT")
                .errorMessage("timeout")
                .build();
    }
}
