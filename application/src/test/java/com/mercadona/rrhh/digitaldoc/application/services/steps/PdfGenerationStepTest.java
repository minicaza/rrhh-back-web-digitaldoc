package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.PdfGeneratorPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfGenerationStepTest {

    @Mock private PdfGeneratorPort pdfGenerator;
    @InjectMocks private PdfGenerationStep step;

    @Test
    void stepMetadata_isCorrect() {
        assertThat(step.fromStatus()).isEqualTo(DocumentStatus.ENRICHED);
        assertThat(step.toStatus()).isEqualTo(DocumentStatus.PDF_GENERATED);
        assertThat(step.failedStep()).isEqualTo(FailedStep.PDF_GENERATION);
        assertThat(step.handlesOwnPersistence()).isFalse();
    }

    @Test
    void execute_setsPdfBytesOnDocument() {
        var doc = enrichedDocument();
        byte[] pdfBytes = new byte[]{1, 2, 3};
        when(pdfGenerator.generate(doc)).thenReturn(pdfBytes);

        step.execute(doc);

        assertThat(doc.getPdfBytes()).isEqualTo(pdfBytes);
        verify(pdfGenerator).generate(doc);
    }

    @Test
    void execute_propagatesExceptionFromGenerator() {
        var doc = enrichedDocument();
        when(pdfGenerator.generate(any())).thenThrow(new RuntimeException("PDF error"));

        assertThatThrownBy(() -> step.execute(doc))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("PDF error");
    }

    private Document enrichedDocument() {
        return Document.builder()
                .id(UUID.randomUUID())
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.ENRICHED)
                .build();
    }
}
