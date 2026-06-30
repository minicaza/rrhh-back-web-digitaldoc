package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentErrorMO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link DocumentErrorMO} entities.
 */
public interface DocumentErrorMOJpaRepository extends JpaRepository<DocumentErrorMO, Long> {

    /**
     * Returns all error records for a given document.
     *
     * @param documentId the document identifier
     * @return list of error records, empty if none
     */
    List<DocumentErrorMO> findByDocumentId(UUID documentId);

    /**
     * Returns the most recent error record for a document, ordered by error_time descending.
     * Used by the pipeline orchestrator to determine which step to resume from.
     *
     * @param documentId the document identifier
     * @return the latest error record, or empty if none
     */
    Optional<DocumentErrorMO> findTopByDocumentIdOrderByErrorTimeDesc(UUID documentId);
}
