package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentStatusNamesMO;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.DocumentStatusNamesPK;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link DocumentStatusNamesMO} entities.
 */
public interface DocumentStatusNamesMOJpaRepository extends JpaRepository<DocumentStatusNamesMO, DocumentStatusNamesPK> {
}
