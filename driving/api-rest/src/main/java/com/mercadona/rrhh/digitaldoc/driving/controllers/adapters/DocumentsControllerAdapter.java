package com.mercadona.rrhh.digitaldoc.driving.controllers.adapters;

import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentQueryPort;
import com.mercadona.rrhh.digitaldoc.definition.server.DocumentsApi;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driving.controllers.mappers.DocumentDTOMapper;
import com.mercadona.rrhh.digitaldoc.model.DocumentByEmployeeResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentByIdResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentStatusResponse;
import com.mercadona.rrhh.digitaldoc.model.SuccessGetDocuments;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controller adapter for the Documents API.
 * Delegates all query operations to {@link DocumentQueryPort}.
 */
@RestController
@AllArgsConstructor
@RequestMapping
public class DocumentsControllerAdapter implements DocumentsApi {

    private static final String DEFAULT_LOCALE = "es-ES";

    private final DocumentQueryPort documentQueryPort;
    private final DocumentDTOMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<DocumentByEmployeeResponse> getDocumentByEmployee(
            String employeeId, String managedGroupId, String localeLanguageCode) {

        var locale = resolveLocale(localeLanguageCode);
        return documentQueryPort.findByEmployee(employeeId, managedGroupId, locale)
            .map(mapper::toDocumentByEmployeeResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<DocumentByIdResponse> getDocumentById(
            UUID documentId, String localeLanguageCode) {

        var locale = resolveLocale(localeLanguageCode);
        return documentQueryPort.findById(documentId, locale)
            .map(mapper::toDocumentByIdResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(
            UUID documentId, String acceptLanguage) {

        var locale = resolveLocale(acceptLanguage);
        return documentQueryPort.getDocumentStatus(documentId, locale)
            .map(mapper::toDocumentStatusResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<SuccessGetDocuments> listDocumentsByStatus(
            com.mercadona.rrhh.digitaldoc.model.DocumentStatus status,
            String localeLanguageCode,
            Integer firstPage,
            Integer pageSize,
            String sort,
            String order) {

        var domainStatus = DocumentStatus.valueOf(status.name());
        var locale = resolveLocale(localeLanguageCode);
        var page = documentQueryPort.listByStatus(domainStatus, locale, firstPage, pageSize, sort, order);
        return ResponseEntity.ok(mapper.toSuccessGetDocuments(page, firstPage));
    }

    /**
     * Returns the provided locale or falls back to the default.
     *
     * @param locale the locale from the request
     * @return the resolved locale
     */
    private String resolveLocale(String locale) {
        return locale != null ? locale : DEFAULT_LOCALE;
    }
}
