package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.PdfGeneratorPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Step 2: generates the PDF certificate from the enriched employee data.
 * Transitions: ENRICHED → PDF_GENERATED.
 */
@Component
@RequiredArgsConstructor
public class PdfGenerationStep implements DocumentStep {

    private final PdfGeneratorPort pdfGenerator;

    @Override
    public DocumentStatus fromStatus() {
        return DocumentStatus.ENRICHED;
    }

    @Override
    public DocumentStatus toStatus() {
        return DocumentStatus.PDF_GENERATED;
    }

    @Override
    public FailedStep failedStep() {
        return FailedStep.PDF_GENERATION;
    }

    @Override
    public void execute(Document document) {
        byte[] bytes = pdfGenerator.generate(document);
        document.setPdfBytes(bytes);
    }
}
