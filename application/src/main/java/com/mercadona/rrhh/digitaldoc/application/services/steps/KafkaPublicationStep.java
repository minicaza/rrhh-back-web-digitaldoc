package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.OutboxPublicationPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Step 4: publishes the document availability event via the transactional outbox.
 *
 * <p>This step handles its own persistence: {@code OutboxPublicationPort.saveAndMarkPublished}
 * atomically inserts the outbox record and updates the document status to PUBLISHED in a single
 * REQUIRES_NEW transaction. The orchestrator must not call {@code updateStatus} afterwards.
 * Transitions: STORED → PUBLISHED.
 */
@Component
@RequiredArgsConstructor
public class KafkaPublicationStep implements DocumentStep {

    private final OutboxPublicationPort outboxPort;

    @Override
    public DocumentStatus fromStatus() {
        return DocumentStatus.STORED;
    }

    @Override
    public DocumentStatus toStatus() {
        return DocumentStatus.PUBLISHED;
    }

    @Override
    public FailedStep failedStep() {
        return FailedStep.PUBLICATION;
    }

    @Override
    public boolean handlesOwnPersistence() {
        return true;
    }

    @Override
    public void execute(Document document) {
        outboxPort.saveAndMarkPublished(document);
    }
}
