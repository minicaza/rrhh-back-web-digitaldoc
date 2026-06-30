package com.mercadona.rrhh.digitaldoc.driven.repositories.jpa;

import com.mercadona.rrhh.digitaldoc.driven.repositories.models.LocaleLanguageMO;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link LocaleLanguageMO} entities.
 */
public interface LocaleLanguageMOJpaRepository extends JpaRepository<LocaleLanguageMO, String> {
}
