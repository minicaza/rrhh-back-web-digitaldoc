package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.rrhh.digitaldoc.domain.Document;

/**
 * Outbound port for generating the PDF certificate of a document.
 * Requires {@code document.employeeInfo} to be populated before calling.
 */
public interface PdfGeneratorPort {

    /**
     * Generates the PDF bytes for the given document.
     *
     * @param document the document with enriched employee data
     * @return the generated PDF as a byte array
     */
    byte[] generate(Document document);
}
