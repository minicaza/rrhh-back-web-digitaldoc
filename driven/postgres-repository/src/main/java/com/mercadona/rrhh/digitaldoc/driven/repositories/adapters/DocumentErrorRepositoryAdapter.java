package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentErrorRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.DocumentError;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentErrorMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.DocumentErrorMOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for document error persistence operations implementing {@link DocumentErrorRepositoryPort}.
 */
@Component
@RequiredArgsConstructor
public class DocumentErrorRepositoryAdapter implements DocumentErrorRepositoryPort {

    private final DocumentErrorMOJpaRepository jpaRepository;
    private final DocumentErrorMOMapper mapper;

    @Override
    public DocumentError save(DocumentError error) {
        return mapper.toDomain(jpaRepository.save(mapper.toMO(error)));
    }

    @Override
    public List<DocumentError> findByDocumentId(UUID documentId) {
        return mapper.toDomainList(jpaRepository.findByDocumentId(documentId));
    }

    @Override
    public Optional<DocumentError> findLatestByDocumentId(UUID documentId) {
        return jpaRepository.findTopByDocumentIdOrderByErrorTimeDesc(documentId)
                .map(mapper::toDomain);
    }
}
