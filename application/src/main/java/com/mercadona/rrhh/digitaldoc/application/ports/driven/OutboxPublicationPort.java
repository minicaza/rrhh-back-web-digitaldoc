package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.rrhh.digitaldoc.domain.Document;

/**
 * Outbound port for publishing document availability events via the transactional outbox.
 */
public interface OutboxPublicationPort {

    /**
     * Atomically saves the outbox event and marks the document as PUBLISHED.
     * Runs in a dedicated REQUIRES_NEW transaction so that the outbox insert
     * and the status update are either both committed or both rolled back.
     *
     * @param document the document to publish
     */
    void saveAndMarkPublished(Document document);
}
