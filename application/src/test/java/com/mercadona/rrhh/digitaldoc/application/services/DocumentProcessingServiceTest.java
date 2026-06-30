package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentProcessingServiceTest {

    @Mock private DocumentRepositoryPort documentRepository;
    @Mock private DocumentPipelineOrchestrator orchestrator;
    @InjectMocks private DocumentProcessingService service;

    @Test
    void processDocuments_dispatchesEachDocumentToOrchestrator() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var doc1 = documentWithId(id1);
        var doc2 = documentWithId(id2);

        when(documentRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(doc1, doc2));

        service.processDocuments(List.of(id1, id2));

        verify(orchestrator).processAsync(id1);
        verify(orchestrator).processAsync(id2);
    }

    @Test
    void processDocuments_emptyList_doesNothing() {
        when(documentRepository.findAllById(List.of())).thenReturn(List.of());

        service.processDocuments(List.of());

        verifyNoInteractions(orchestrator);
    }

    @Test
    void processDocuments_repositoryReturnsSubset_dispatchesOnlyFoundDocuments() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var doc1 = documentWithId(id1);

        when(documentRepository.findAllById(List.of(id1, id2))).thenReturn(List.of(doc1));

        service.processDocuments(List.of(id1, id2));

        verify(orchestrator).processAsync(id1);
        verify(orchestrator, never()).processAsync(id2);
    }

    private Document documentWithId(UUID id) {
        return Document.builder()
                .id(id)
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.PENDING)
                .build();
    }
}
