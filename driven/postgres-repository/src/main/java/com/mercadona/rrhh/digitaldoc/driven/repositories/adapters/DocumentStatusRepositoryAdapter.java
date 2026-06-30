package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentStatusRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentStatusNamesMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.DocumentStatusNamesPK;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adapter for document status localisation lookups implementing {@link DocumentStatusRepositoryPort}.
 */
@Component
@RequiredArgsConstructor
public class DocumentStatusRepositoryAdapter implements DocumentStatusRepositoryPort {

    private final DocumentStatusNamesMOJpaRepository jpaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> findStatusDescription(DocumentStatus status, String locale) {
        return jpaRepository.findById(new DocumentStatusNamesPK(status.getId(), locale))
            .map(mo -> mo.getDescription());
    }
}
