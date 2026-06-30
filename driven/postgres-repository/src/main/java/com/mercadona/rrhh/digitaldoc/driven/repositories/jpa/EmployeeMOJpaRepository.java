package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.EmployeeMO;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.EmployeePK;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link EmployeeMO} entities.
 */
public interface EmployeeMOJpaRepository extends JpaRepository<EmployeeMO, EmployeePK> {
}
