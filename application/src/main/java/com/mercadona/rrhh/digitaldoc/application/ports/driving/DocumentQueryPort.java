package com.mercadona.rrhh.digitaldoc.application.ports.driving;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatusInfo;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;

import java.util.Optional;
import java.util.UUID;

/**
 * Inbound port for document query use cases.
 */
public interface DocumentQueryPort {

    /**
     * Returns the document for a given employee and managed group, including the localised status name.
     *
     * @param employeeId     the employee identifier
     * @param managedGroupId the managed group identifier
     * @param locale         locale code for the status name (e.g. es-ES)
     * @return an Optional containing the document view if found
     */
    Optional<DocumentView> findByEmployee(String employeeId, String managedGroupId, String locale);

    /**
     * Returns a document by its unique identifier, including the localised status name.
     *
     * @param documentId the document UUID
     * @param locale     locale code for the status name (e.g. es-ES)
     * @return an Optional containing the document view if found
     */
    Optional<DocumentView> findById(UUID documentId, String locale);

    /**
     * Returns a paginated list of documents matching the given status, including localised status names.
     *
     * @param status    the lifecycle status to filter by
     * @param locale    locale code for the status name (e.g. es-ES)
     * @param firstPage requested page number (1-based)
     * @param pageSize  number of results per page
     * @param sort      field to sort by (employeeId or managedGroupId)
     * @param order     sort direction (asc or desc)
     * @return a page of matching document views
     */
    MercadonaPage<DocumentView> listByStatus(
        DocumentStatus status, String locale, int firstPage, int pageSize, String sort, String order);

    /**
     * Returns the current status of a document with its localised description.
     *
     * @param documentId the document UUID
     * @param locale     locale code for the description (e.g. es-ES)
     * @return an Optional containing the status info if the document exists
     */
    Optional<DocumentStatusInfo> getDocumentStatus(UUID documentId, String locale);
}
