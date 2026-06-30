package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.services.steps.DocumentStep;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Executes the document processing pipeline asynchronously.
 *
 * <p>Each step is attempted once. On failure the document is marked FAILED and an error
 * record is persisted to {@code document_error}. The BTC batch handles reprocessing of
 * FAILED documents on its scheduled run.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentPipelineOrchestrator {

    private final List<DocumentStep> steps;
    private final DocumentRepositoryPort documentRepository;

    @Async("documentExecutor")
    public void processAsync(UUID documentId) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        Optional<DocumentStep> applicableStep;
        while ((applicableStep = findApplicableStep(doc)).isPresent()) {
            DocumentStep step = applicableStep.get();
            try {
                step.execute(doc);
                if (!step.handlesOwnPersistence()) {
                    documentRepository.updateStatus(doc.getId(), step.toStatus());
                }
                doc.setStatus(step.toStatus());
            } catch (Exception ex) {
                log.error("Step {} failed for document {}: {}", step.failedStep(), documentId, ex.getMessage(), ex);
                documentRepository.markFailed(doc.getId(), step.failedStep(), ex.getMessage());
                return;
            }
        }
    }

    private Optional<DocumentStep> findApplicableStep(Document doc) {
        if (doc.getStatus() == DocumentStatus.PUBLISHED || doc.getStatus() == DocumentStatus.FAILED) {
            return Optional.empty();
        }
        return steps.stream()
                .filter(s -> s.fromStatus() == doc.getStatus())
                .findFirst();
    }
}
