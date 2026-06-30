package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.BucketStoragePort;
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
class BucketStorageStepTest {

    @Mock private BucketStoragePort bucketStorage;
    @InjectMocks private BucketStorageStep step;

    @Test
    void stepMetadata_isCorrect() {
        assertThat(step.fromStatus()).isEqualTo(DocumentStatus.PDF_GENERATED);
        assertThat(step.toStatus()).isEqualTo(DocumentStatus.STORED);
        assertThat(step.failedStep()).isEqualTo(FailedStep.STORAGE);
        assertThat(step.handlesOwnPersistence()).isFalse();
    }

    @Test
    void execute_uploadsDocumentPdfToBucket() {
        var docId = UUID.randomUUID();
        byte[] pdfBytes = new byte[]{4, 5, 6};
        var doc = Document.builder()
                .id(docId)
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.PDF_GENERATED)
                .pdfBytes(pdfBytes)
                .build();

        step.execute(doc);

        verify(bucketStorage).upload(docId, pdfBytes);
    }

    @Test
    void execute_propagatesExceptionFromBucket() {
        var doc = pdfGeneratedDocument();
        doThrow(new RuntimeException("Bucket error")).when(bucketStorage).upload(any(), any());

        assertThatThrownBy(() -> step.execute(doc))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Bucket error");
    }

    private Document pdfGeneratedDocument() {
        return Document.builder()
                .id(UUID.randomUUID())
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.PDF_GENERATED)
                .pdfBytes(new byte[]{1})
                .build();
    }
}
