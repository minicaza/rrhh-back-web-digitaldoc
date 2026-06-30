package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for document persistence operations.
 */
public interface DocumentRepositoryPort {

    /**
     * Persists a document.
     *
     * @param document the document to save
     * @return the saved document
     */
    Document save(Document document);

    /**
     * Finds a document by its unique identifier.
     *
     * @param id the document UUID
     * @return an Optional containing the document if found
     */
    Optional<Document> findById(UUID id);

    /**
     * Finds the document belonging to a given employee in a given managed group.
     *
     * @param employeeId      the employee identifier
     * @param managedGroupId  the managed group identifier
     * @return an Optional containing the document if found
     */
    Optional<Document> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

    /**
     * Returns a paginated list of documents matching the given status.
     *
     * @param status   the document lifecycle status to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of matching documents
     */
    MercadonaPage<Document> findByStatus(DocumentStatus status, Pageable pageable);
}
