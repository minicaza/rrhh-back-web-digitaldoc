package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.BucketStoragePort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Step 3: uploads the generated PDF to the object storage bucket.
 * Path is always {@code documents/{documentId}.pdf} (deterministic — retries overwrite the same object).
 * Transitions: PDF_GENERATED → STORED.
 */
@Component
@RequiredArgsConstructor
public class BucketStorageStep implements DocumentStep {

    private final BucketStoragePort bucketStorage;

    @Override
    public DocumentStatus fromStatus() {
        return DocumentStatus.PDF_GENERATED;
    }

    @Override
    public DocumentStatus toStatus() {
        return DocumentStatus.STORED;
    }

    @Override
    public FailedStep failedStep() {
        return FailedStep.STORAGE;
    }

    @Override
    public void execute(Document document) {
        bucketStorage.upload(document.getId(), document.getPdfBytes());
    }
}
