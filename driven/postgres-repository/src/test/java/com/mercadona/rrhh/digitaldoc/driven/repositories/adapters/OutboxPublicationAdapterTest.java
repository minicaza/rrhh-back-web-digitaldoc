package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.framework.cna.lib.outbox.avro.jpa.register.service.OutBoxAvroJPAService;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.mappers.EmployeeDigitalDocumentOutboxMapper;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentMO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocument;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxPublicationAdapterTest {

    private static final String TOPIC = "concepto.thirdparty.employee.employeedigitaldocument.event.public.v0";
    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    @Mock OutBoxAvroJPAService outboxService;
    @Mock DocumentMOJpaRepository documentJpaRepository;
    @Mock EmployeeDigitalDocumentOutboxMapper mapper;

    OutboxPublicationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new OutboxPublicationAdapter(outboxService, documentJpaRepository, mapper, TOPIC);
    }

    @Test
    void saveAndMarkPublished_savesToOutboxWithCorrectKeyValueAndTopic() {
        Document document = document();
        EmployeeDigitalDocumentEventRestrictedOutKey key = avroKey();
        EmployeeDigitalDocumentEventRestrictedOutValue value = avroValue();
        DocumentMO mo = documentMO(DocumentStatus.STORED);

        when(mapper.toKey(document)).thenReturn(key);
        when(mapper.toValue(document)).thenReturn(value);
        when(documentJpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(mo));

        adapter.saveAndMarkPublished(document);

        verify(outboxService).save(key, value, TOPIC);
    }

    @Test
    void saveAndMarkPublished_updatesDocumentStatusToPublished() {
        Document document = document();
        DocumentMO mo = documentMO(DocumentStatus.STORED);

        when(mapper.toKey(document)).thenReturn(avroKey());
        when(mapper.toValue(document)).thenReturn(avroValue());
        when(documentJpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.of(mo));

        adapter.saveAndMarkPublished(document);

        assertThat(mo.getDocumentStatusId()).isEqualTo(DocumentStatus.PUBLISHED.getId());
        verify(documentJpaRepository).save(mo);
    }

    @Test
    void saveAndMarkPublished_stillSavesToOutbox_whenDocumentMoNotFound() {
        Document document = document();

        when(mapper.toKey(document)).thenReturn(avroKey());
        when(mapper.toValue(document)).thenReturn(avroValue());
        when(documentJpaRepository.findById(DOCUMENT_ID)).thenReturn(Optional.empty());

        adapter.saveAndMarkPublished(document);

        verify(outboxService).save(any(), any(), any());
        verify(documentJpaRepository, never()).save(any());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Document document() {
        return Document.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .status(DocumentStatus.STORED)
                .build();
    }

    private DocumentMO documentMO(DocumentStatus status) {
        return DocumentMO.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .documentStatusId(status.getId())
                .build();
    }

    private EmployeeDigitalDocumentEventRestrictedOutKey avroKey() {
        return EmployeeDigitalDocumentEventRestrictedOutKey.newBuilder()
                .setEmployeeId("E001")
                .setManagedGroupId("08")
                .build();
    }

    private EmployeeDigitalDocumentEventRestrictedOutValue avroValue() {
        return EmployeeDigitalDocumentEventRestrictedOutValue.newBuilder()
                .setPayload(EmployeeDigitalDocument.newBuilder().build())
                .build();
    }
}
