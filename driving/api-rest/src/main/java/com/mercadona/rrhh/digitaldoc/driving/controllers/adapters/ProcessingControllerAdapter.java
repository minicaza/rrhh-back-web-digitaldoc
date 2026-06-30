package com.mercadona.rrhh.digitaldoc.driving.controllers.adapters;

import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentProcessingPort;
import com.mercadona.rrhh.digitaldoc.definition.server.ProcessingApi;
import com.mercadona.rrhh.digitaldoc.model.ProcessDocumentsRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller adapter for the Processing API.
 * Delegates processing triggers to {@link DocumentProcessingPort}.
 */
@RestController
@AllArgsConstructor
@RequestMapping
public class ProcessingControllerAdapter implements ProcessingApi {

    private final DocumentProcessingPort documentProcessingPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Void> processDocuments(ProcessDocumentsRequest processDocumentsRequest) {
        documentProcessingPort.processDocuments(processDocumentsRequest.getDocumentIds());
        return ResponseEntity.ok().build();
    }
}
