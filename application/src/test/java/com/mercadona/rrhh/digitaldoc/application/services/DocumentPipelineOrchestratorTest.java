package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentErrorRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.services.steps.DocumentStep;
import com.mercadona.rrhh.digitaldoc.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentPipelineOrchestratorTest {

    @Mock private DocumentStep enrichmentStep;
    @Mock private DocumentStep pdfStep;
    @Mock private DocumentStep bucketStep;
    @Mock private DocumentStep kafkaStep;
    @Mock private DocumentRepositoryPort documentRepository;
    @Mock private DocumentErrorRepositoryPort documentErrorRepository;

    private DocumentPipelineOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        configureStep(enrichmentStep, DocumentStatus.PENDING,      DocumentStatus.ENRICHED,      FailedStep.ENRICHMENT,   false);
        configureStep(pdfStep,        DocumentStatus.ENRICHED,     DocumentStatus.PDF_GENERATED,  FailedStep.PDF_GENERATION, false);
        configureStep(bucketStep,     DocumentStatus.PDF_GENERATED, DocumentStatus.STORED,        FailedStep.STORAGE,      false);
        configureStep(kafkaStep,      DocumentStatus.STORED,        DocumentStatus.PUBLISHED,     FailedStep.PUBLICATION,  true);

        orchestrator = new DocumentPipelineOrchestrator(
                List.of(enrichmentStep, pdfStep, bucketStep, kafkaStep),
                documentRepository,
                documentErrorRepository
        );
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void processAsync_pendingDocument_runsAllStepsInOrder() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PENDING);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        orchestrator.processAsync(docId);

        var inOrder = inOrder(enrichmentStep, pdfStep, bucketStep, kafkaStep);
        inOrder.verify(enrichmentStep).execute(doc);
        inOrder.verify(pdfStep).execute(doc);
        inOrder.verify(bucketStep).execute(doc);
        inOrder.verify(kafkaStep).execute(doc);
    }

    @Test
    void processAsync_stepsWithoutOwnPersistence_updateStatusAfterEachStep() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PENDING);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        orchestrator.processAsync(docId);

        verify(documentRepository).updateStatus(docId, DocumentStatus.ENRICHED);
        verify(documentRepository).updateStatus(docId, DocumentStatus.PDF_GENERATED);
        verify(documentRepository).updateStatus(docId, DocumentStatus.STORED);
    }

    @Test
    void processAsync_kafkaStepHandlesOwnPersistence_updateStatusNotCalledForPublished() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PENDING);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        orchestrator.processAsync(docId);

        verify(documentRepository, never()).updateStatus(docId, DocumentStatus.PUBLISHED);
    }

    // ── document not found ────────────────────────────────────────────────────

    @Test
    void processAsync_documentNotFound_throwsIllegalArgument() {
        var docId = UUID.randomUUID();
        when(documentRepository.findById(docId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orchestrator.processAsync(docId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(docId.toString());
    }

    // ── step failure ──────────────────────────────────────────────────────────

    @Test
    void processAsync_enrichmentStepFails_marksFailedAndStopsProcessing() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PENDING);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        doThrow(new RuntimeException("API error")).when(enrichmentStep).execute(doc);

        orchestrator.processAsync(docId);

        verify(documentRepository).markFailed(docId, FailedStep.ENRICHMENT, "API error");
        verifyNoInteractions(pdfStep, bucketStep, kafkaStep);
    }

    @Test
    void processAsync_pdfStepFails_marksFailedWithCorrectStep() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PENDING);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        doThrow(new RuntimeException("PDF error")).when(pdfStep).execute(doc);

        orchestrator.processAsync(docId);

        verify(documentRepository).markFailed(docId, FailedStep.PDF_GENERATION, "PDF error");
        verifyNoInteractions(bucketStep, kafkaStep);
    }

    // ── resume from FAILED ────────────────────────────────────────────────────

    @Test
    void processAsync_failedDocument_resumesFromLastFailedStep() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.FAILED);
        var error = DocumentError.builder()
                .documentId(docId)
                .failedStep("PDF_GENERATION")
                .build();

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        when(documentErrorRepository.findLatestByDocumentId(docId)).thenReturn(Optional.of(error));

        orchestrator.processAsync(docId);

        verify(pdfStep).execute(doc);
        verify(enrichmentStep, never()).execute(any());
        verify(bucketStep).execute(doc);
        verify(kafkaStep).execute(doc);
    }

    @Test
    void processAsync_failedDocumentWithUnknownStep_noStepsExecuted() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.FAILED);
        var error = DocumentError.builder()
                .documentId(docId)
                .failedStep("UNKNOWN_STEP")
                .build();

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        when(documentErrorRepository.findLatestByDocumentId(docId)).thenReturn(Optional.of(error));

        orchestrator.processAsync(docId);

        verify(enrichmentStep, never()).execute(any());
        verify(pdfStep, never()).execute(any());
        verify(bucketStep, never()).execute(any());
        verify(kafkaStep, never()).execute(any());
    }

    @Test
    void processAsync_failedDocumentWithNoErrorRecord_noStepsExecuted() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.FAILED);

        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));
        when(documentErrorRepository.findLatestByDocumentId(docId)).thenReturn(Optional.empty());

        orchestrator.processAsync(docId);

        verify(enrichmentStep, never()).execute(any());
        verify(pdfStep, never()).execute(any());
        verify(bucketStep, never()).execute(any());
        verify(kafkaStep, never()).execute(any());
    }

    // ── already published ─────────────────────────────────────────────────────

    @Test
    void processAsync_publishedDocument_noStepsExecuted() {
        var docId = UUID.randomUUID();
        var doc = documentWithStatus(docId, DocumentStatus.PUBLISHED);
        when(documentRepository.findById(docId)).thenReturn(Optional.of(doc));

        orchestrator.processAsync(docId);

        verifyNoInteractions(enrichmentStep, pdfStep, bucketStep, kafkaStep);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void configureStep(DocumentStep step, DocumentStatus from, DocumentStatus to,
                                FailedStep failedStep, boolean handlesOwnPersistence) {
        lenient().when(step.fromStatus()).thenReturn(from);
        lenient().when(step.toStatus()).thenReturn(to);
        lenient().when(step.failedStep()).thenReturn(failedStep);
        lenient().when(step.handlesOwnPersistence()).thenReturn(handlesOwnPersistence);
    }

    private Document documentWithStatus(UUID id, DocumentStatus status) {
        return Document.builder()
                .id(id)
                .employeeId("1234567")
                .managedGroupId("08")
                .status(status)
                .build();
    }
}
