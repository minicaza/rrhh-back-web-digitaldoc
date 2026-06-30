package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters.stubs;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.PdfGeneratorPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import org.springframework.stereotype.Component;

/** Temporary stub — replace with PDFBox implementation. */
@Component
public class PdfGeneratorStubAdapter implements PdfGeneratorPort {

    @Override
    public byte[] generate(Document document) {
        throw new UnsupportedOperationException("PdfGeneratorPort not implemented yet");
    }
}
