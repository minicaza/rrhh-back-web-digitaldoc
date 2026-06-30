package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.rrhh.digitaldoc.domain.DocumentError;

import java.util.List;
import java.util.UUID;

/**
 * Outbound port for document error persistence operations.
 */
public interface DocumentErrorRepositoryPort {

    /**
     * Persists a document error record.
     *
     * @param error the error to save
     * @return the saved error
     */
    DocumentError save(DocumentError error);

    /**
     * Returns all error records for a given document.
     *
     * @param documentId the document identifier
     * @return list of errors for the document, empty if none
     */
    List<DocumentError> findByDocumentId(UUID documentId);

    /**
     * Returns the most recent error record for a document, used by the pipeline
     * orchestrator to determine which step to resume from after a FAILED status.
     *
     * @param documentId the document identifier
     * @return the latest error, or empty if none exist
     */
    java.util.Optional<DocumentError> findLatestByDocumentId(UUID documentId);
}
