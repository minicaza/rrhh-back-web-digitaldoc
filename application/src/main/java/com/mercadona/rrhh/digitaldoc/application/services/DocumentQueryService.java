package com.mercadona.rrhh.digitaldoc.application.services;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentStatusRepositoryPort;
import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentQueryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatusInfo;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service that implements document query use cases.
 * Orchestrates {@link DocumentRepositoryPort} and {@link DocumentStatusRepositoryPort}
 * to return enriched projections with localised status names.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentQueryService implements DocumentQueryPort {

    private final DocumentRepositoryPort documentRepositoryPort;
    private final DocumentStatusRepositoryPort documentStatusRepositoryPort;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DocumentView> findByEmployee(String employeeId, String managedGroupId, String locale) {
        return documentRepositoryPort
            .findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId)
            .map(doc -> toView(doc, locale));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DocumentView> findById(UUID documentId, String locale) {
        return documentRepositoryPort
            .findById(documentId)
            .map(doc -> toView(doc, locale));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MercadonaPage<DocumentView> listByStatus(
            DocumentStatus status, String locale, int firstPage, int pageSize, String sort, String order) {

        var direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        var pageable = PageRequest.of(firstPage - 1, pageSize, Sort.by(direction, sort));
        var page = documentRepositoryPort.findByStatus(status, pageable);
        return MercadonaPage.of(page.map(doc -> toView(doc, locale)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DocumentStatusInfo> getDocumentStatus(UUID documentId, String locale) {
        return documentRepositoryPort
            .findById(documentId)
            .map(doc -> {
                var description = documentStatusRepositoryPort
                    .findStatusDescription(doc.getStatus(), locale)
                    .orElse(doc.getStatus().name());
                return new DocumentStatusInfo(doc.getStatus(), description);
            });
    }

    /**
     * Combines a document with its localised status name into a {@link DocumentView}.
     * Falls back to the enum constant name if no description is found for the given locale.
     *
     * @param doc    the document
     * @param locale the locale for the status name
     * @return the enriched projection
     */
    private DocumentView toView(Document doc, String locale) {
        var statusName = documentStatusRepositoryPort
            .findStatusDescription(doc.getStatus(), locale)
            .orElse(doc.getStatus().name());
        return new DocumentView(doc, statusName);
    }
}
