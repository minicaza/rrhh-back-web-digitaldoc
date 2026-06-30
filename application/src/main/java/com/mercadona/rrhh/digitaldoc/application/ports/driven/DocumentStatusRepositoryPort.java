package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;

import java.util.Optional;

/**
 * Outbound port for document status localisation lookups.
 */
public interface DocumentStatusRepositoryPort {

    /**
     * Returns the localised description for a given status and locale.
     *
     * @param status the document status
     * @param locale the locale code (e.g. es-ES)
     * @return an Optional containing the description if found for the given locale
     */
    Optional<String> findStatusDescription(DocumentStatus status, String locale);
}
