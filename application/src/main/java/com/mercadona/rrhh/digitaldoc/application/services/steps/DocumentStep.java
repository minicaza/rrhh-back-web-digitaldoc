package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;

/**
 * Strategy interface for a single step in the document processing pipeline.
 *
 * <p>Each implementation handles one state transition: it reads {@code fromStatus()},
 * performs I/O (no transaction open during the call), and lets the orchestrator
 * persist the new status via a short REQUIRES_NEW transaction.
 *
 * <p>{@link #handlesOwnPersistence()} is {@code true} only for {@code KafkaPublicationStep},
 * which must atomically save the outbox record and mark the document as PUBLISHED in one tx.
 */
public interface DocumentStep {

    /** The document must be in this status for this step to apply. */
    DocumentStatus fromStatus();

    /** The status the document transitions to on success. */
    DocumentStatus toStatus();

    /** Identifies this step in the document_error table on failure. */
    FailedStep failedStep();

    /**
     * Executes the step logic. May mutate transient pipeline fields on {@code document}
     * (e.g. {@code employeeInfo}, {@code pdfBytes}).
     * Must not open a database transaction: I/O only.
     *
     * @param document the document to process
     */
    void execute(Document document);

    /**
     * Returns {@code true} if this step commits its own status change (e.g. outbox + markPublished).
     * When {@code true}, the orchestrator skips its own {@code updateStatus} call.
     */
    default boolean handlesOwnPersistence() {
        return false;
    }
}
