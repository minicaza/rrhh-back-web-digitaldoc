package com.mercadona.rrhh.digitaldoc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain entity representing a processing error recorded for a document.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentError {

    /** Sequential identifier of the error record. */
    private Long id;

    /** Identifier of the document that failed. */
    private UUID documentId;

    /** Pipeline step where the failure occurred (e.g. ENRICHMENT, PDF_GENERATION, STORAGE, PUBLICATION). */
    private String failedStep;

    /** Detail of the error produced. */
    private String errorMessage;

    /** Timestamp when the error was recorded. */
    private OffsetDateTime errorTime;
}
