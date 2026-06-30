package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentMO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link DocumentMO} entities.
 */
public interface DocumentMOJpaRepository extends JpaRepository<DocumentMO, UUID> {

    /**
     * Finds the document for a given employee and managed group.
     *
     * @param employeeId     the employee identifier
     * @param managedGroupId the managed group identifier
     * @return an Optional containing the document if found
     */
    Optional<DocumentMO> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

    /**
     * Returns a paginated list of documents with the given status id.
     *
     * @param documentStatusId the numeric status identifier
     * @param pageable         pagination parameters
     * @return a page of matching documents
     */
    Page<DocumentMO> findByDocumentStatusId(Short documentStatusId, Pageable pageable);
}
