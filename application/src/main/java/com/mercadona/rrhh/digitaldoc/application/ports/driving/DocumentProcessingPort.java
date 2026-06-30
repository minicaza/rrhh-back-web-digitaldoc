package com.mercadona.rrhh.digitaldoc.application.ports.driving;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port for the document processing trigger use case.
 */
public interface DocumentProcessingPort {

    /**
     * Accepts a list of document UUIDs and submits each one for asynchronous pipeline processing.
     *
     * @param documentIds list of document UUIDs to process
     */
    void processDocuments(List<UUID> documentIds);
}
