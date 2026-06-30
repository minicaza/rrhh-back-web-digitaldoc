package com.mercadona.rrhh.digitaldoc.driving.controllers.adapters;

import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentProcessingPort;
import com.mercadona.rrhh.digitaldoc.model.ProcessDocumentsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessingControllerAdapterTest {

    @Mock
    DocumentProcessingPort documentProcessingPort;

    @InjectMocks
    ProcessingControllerAdapter adapter;

    @Test
    void processDocuments_delegatesToPort_andReturnsOk() {
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID());
        var request = new ProcessDocumentsRequest();
        request.setDocumentIds(ids);

        var result = adapter.processDocuments(request);

        verify(documentProcessingPort).processDocuments(ids);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
    }

    @Test
    void processDocuments_delegatesEmptyList_andReturnsOk() {
        var request = new ProcessDocumentsRequest();
        request.setDocumentIds(List.of());

        var result = adapter.processDocuments(request);

        verify(documentProcessingPort).processDocuments(List.of());
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
