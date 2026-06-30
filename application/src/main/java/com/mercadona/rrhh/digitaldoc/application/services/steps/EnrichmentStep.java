package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.EnrichmentClientPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Step 1: fetches employee data from the cardgenerator API and attaches it to the document.
 * Transitions: PENDING → ENRICHED.
 */
@Component
@RequiredArgsConstructor
public class EnrichmentStep implements DocumentStep {

    private final EnrichmentClientPort enrichmentClient;

    @Override
    public DocumentStatus fromStatus() {
        return DocumentStatus.PENDING;
    }

    @Override
    public DocumentStatus toStatus() {
        return DocumentStatus.ENRICHED;
    }

    @Override
    public FailedStep failedStep() {
        return FailedStep.ENRICHMENT;
    }

    @Override
    public void execute(Document document) {
        var info = enrichmentClient.fetch(document.getEmployeeId(), document.getManagedGroupId());
        document.setEmployeeInfo(info);
    }
}
