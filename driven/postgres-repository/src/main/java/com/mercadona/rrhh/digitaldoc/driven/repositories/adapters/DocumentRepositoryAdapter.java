package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.DocumentMOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for document persistence operations implementing {@link DocumentRepositoryPort}.
 */
@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentMOJpaRepository jpaRepository;
    private final DocumentMOMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Document save(Document document) {
        return mapper.toDomain(jpaRepository.save(mapper.toMO(document)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Document> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId) {
        return jpaRepository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId)
            .map(mapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MercadonaPage<Document> findByStatus(DocumentStatus status, Pageable pageable) {
        return MercadonaPage.of(
            jpaRepository.findByDocumentStatusId(status.getId(), pageable)
                .map(mapper::toDomain)
        );
    }
}
