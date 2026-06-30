package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.DocumentRepositoryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentErrorMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.DocumentMOMapper;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentErrorMO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for document persistence operations implementing {@link DocumentRepositoryPort}.
 */
@Component
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepositoryPort {

    private final DocumentMOJpaRepository jpaRepository;
    private final DocumentErrorMOJpaRepository errorJpaRepository;
    private final DocumentMOMapper mapper;

    @Override
    public Document save(Document document) {
        return mapper.toDomain(jpaRepository.save(mapper.toMO(document)));
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Document> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId) {
        return jpaRepository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId)
                .map(mapper::toDomain);
    }

    @Override
    public MercadonaPage<Document> findByStatus(DocumentStatus status, Pageable pageable) {
        return MercadonaPage.of(
                jpaRepository.findByDocumentStatusId(status.getId(), pageable)
                        .map(mapper::toDomain)
        );
    }

    @Override
    public List<Document> findAllById(List<UUID> ids) {
        return mapper.toDomainList(jpaRepository.findAllById(ids));
    }

    /**
     * Loads the entity, sets the new status and saves through the JPA session so that
     * Hibernate Envers can intercept the change and write to {@code document_aud}.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(UUID id, DocumentStatus status) {
        jpaRepository.findById(id).ifPresent(mo -> {
            mo.setDocumentStatusId(status.getId());
            jpaRepository.save(mo);
        });
    }

    /**
     * Atomically marks the document as FAILED (via JPA save — audited by Envers)
     * and inserts an error record. Runs in REQUIRES_NEW so the failure is always
     * persisted regardless of the outer transaction state.
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(UUID id, FailedStep failedStep, String errorMessage) {
        jpaRepository.findById(id).ifPresent(mo -> {
            mo.setDocumentStatusId(DocumentStatus.FAILED.getId());
            jpaRepository.save(mo);
        });
        errorJpaRepository.save(DocumentErrorMO.builder()
                .documentId(id)
                .failedStep(failedStep.name())
                .errorMessage(errorMessage)
                .build());
    }
}
