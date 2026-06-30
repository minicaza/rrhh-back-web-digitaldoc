package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentProcessingPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Application service that implements the document processing trigger use case.
 *
 * TODO: Inject DocumentRepositoryPort and the downstream pipeline ports:
 *       EnrichmentPort, PdfGeneratorPort, StoragePort, PublisherPort.
 */
@Service
public class DocumentProcessingService implements DocumentProcessingPort {

    @Override
    public void processDocuments(List<UUID> documentIds) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
