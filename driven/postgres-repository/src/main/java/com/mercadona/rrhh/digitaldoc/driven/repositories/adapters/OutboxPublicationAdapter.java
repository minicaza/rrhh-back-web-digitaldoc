package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.outbox.avro.jpa.register.service.OutBoxAvroJPAService;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.OutboxPublicationPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.EmployeeDigitalDocumentOutboxMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter that implements the transactional outbox pattern for publishing
 * {@code EmployeeDigitalDocument} availability events to Kafka.
 *
 * <p>{@link #saveAndMarkPublished} runs in a dedicated {@code REQUIRES_NEW} transaction
 * that atomically inserts the outbox record and updates the document status to PUBLISHED.
 * The status is updated via JPA {@code save()} so that Hibernate Envers audits the change.
 * The outbox relay will pick up the record only after this transaction commits.
 */
@Component
public class OutboxPublicationAdapter implements OutboxPublicationPort {

    private final OutBoxAvroJPAService outboxService;
    private final DocumentMOJpaRepository documentJpaRepository;
    private final EmployeeDigitalDocumentOutboxMapper mapper;
    private final String topic;

    public OutboxPublicationAdapter(
            @Qualifier("sr-basic") OutBoxAvroJPAService outboxService,
            DocumentMOJpaRepository documentJpaRepository,
            EmployeeDigitalDocumentOutboxMapper mapper,
            @Value("${outbox.topic.employee-digital-document}") String topic) {
        this.outboxService = outboxService;
        this.documentJpaRepository = documentJpaRepository;
        this.mapper = mapper;
        this.topic = topic;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAndMarkPublished(Document document) {
        outboxService.save(mapper.toKey(document), mapper.toValue(document), topic);
        documentJpaRepository.findById(document.getId()).ifPresent(mo -> {
            mo.setDocumentStatusId(DocumentStatus.PUBLISHED.getId());
            documentJpaRepository.save(mo);
        });
    }
}
