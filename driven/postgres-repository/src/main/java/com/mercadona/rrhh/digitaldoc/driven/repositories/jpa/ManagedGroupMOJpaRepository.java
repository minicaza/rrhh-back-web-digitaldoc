package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.ManagedGroupMO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link ManagedGroupMO} entities.
 */
public interface ManagedGroupMOJpaRepository extends JpaRepository<ManagedGroupMO, String> {
}
