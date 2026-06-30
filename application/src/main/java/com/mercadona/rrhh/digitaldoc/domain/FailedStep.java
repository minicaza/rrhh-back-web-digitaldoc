package com.mercadona.rrhh.digitaldoc.domain;

/**
 * Pipeline step in which a document processing failure occurred.
 * Stored as a string in document_error.failed_step.
 */
public enum FailedStep {
    ENRICHMENT,
    PDF_GENERATION,
    STORAGE,
    PUBLICATION
}
