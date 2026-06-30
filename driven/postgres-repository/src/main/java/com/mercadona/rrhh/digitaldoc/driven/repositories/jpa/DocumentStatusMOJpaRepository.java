package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentStatusMO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link DocumentStatusMO} entities.
 */
public interface DocumentStatusMOJpaRepository extends JpaRepository<DocumentStatusMO, Short> {
}
