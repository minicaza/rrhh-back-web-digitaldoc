package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentProcessingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Accepts a batch of document UUIDs and dispatches each one to the async pipeline.
 * The orchestrator handles every status correctly:
 * documents already PUBLISHED have no applicable step and exit immediately without side effects.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentProcessingService implements DocumentProcessingPort {

    private final DocumentRepositoryPort documentRepository;
    private final DocumentPipelineOrchestrator orchestrator;

    @Override
    public void processDocuments(List<UUID> documentIds) {
        documentRepository.findAllById(documentIds).forEach(doc -> {
            log.debug("Dispatching document {} (status={}) to pipeline", doc.getId(), doc.getStatus());
            orchestrator.processAsync(doc.getId());
        });
    }
}
