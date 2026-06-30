package com.mercadona.rrhh.digitaldoc.driven.repositories.mappers;

import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import org.junit.jupiter.api.Test;
import thirdparty.employee.employeedigitaldocument.v0.DataRecord;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EmployeeDigitalDocumentOutboxMapperTest {

    private final EmployeeDigitalDocumentOutboxMapper mapper = new EmployeeDigitalDocumentOutboxMapper();

    private static final UUID DOCUMENT_ID = UUID.fromString("a3f1c2d4-5678-4abc-9def-0123456789ab");

    // ── toKey ─────────────────────────────────────────────────────────────────

    @Test
    void toKey_mapsEmployeeIdAndManagedGroupId() {
        Document doc = document();

        EmployeeDigitalDocumentEventRestrictedOutKey key = mapper.toKey(doc);

        assertThat(key.getEmployeeId()).isEqualTo("E001");
        assertThat(key.getManagedGroupId()).isEqualTo("08");
    }

    // ── toValue ───────────────────────────────────────────────────────────────

    @Test
    void toValue_payloadContainsEmployeeData() {
        Document doc = document();

        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(doc);

        DataRecord data = value.getPayload().getData();
        assertThat(data.getEmployeeData().getId()).isEqualTo("E001");
        assertThat(data.getEmployeeData().getManagedGroupId().getId()).isEqualTo("08");
    }

    @Test
    void toValue_payloadContainsDocumentRecord() {
        Document doc = document();

        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(doc);

        DataRecord data = value.getPayload().getData();
        assertThat(data.getEmployeeDigitalDocuments()).hasSize(1);
        assertThat(data.getEmployeeDigitalDocuments().get(0).getId()).isEqualTo(DOCUMENT_ID.toString());
    }

    @Test
    void toValue_documentRecordStatusIsPublished() {
        Document doc = document();

        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(doc);

        String status = value.getPayload().getData()
                .getEmployeeDigitalDocuments().get(0)
                .getDigitalDocumentStatus().getId();
        assertThat(status).isEqualTo("PUBLISHED");
    }

    @Test
    void toValue_documentRecordCreationDateFromCreatedAt() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2024-01-15T10:30:00+01:00");
        Document doc = Document.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .status(DocumentStatus.STORED)
                .createdAt(createdAt)
                .build();

        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(doc);

        String creationDate = value.getPayload().getData()
                .getEmployeeDigitalDocuments().get(0)
                .getCreationDate();
        assertThat(creationDate).isEqualTo(createdAt.toString());
    }

    @Test
    void toValue_creationDateIsNull_whenCreatedAtIsNull() {
        Document doc = Document.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .status(DocumentStatus.STORED)
                .createdAt(null)
                .build();

        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(doc);

        String creationDate = value.getPayload().getData()
                .getEmployeeDigitalDocuments().get(0)
                .getCreationDate();
        assertThat(creationDate).isNull();
    }

    @Test
    void toValue_metadataIsPresent() {
        EmployeeDigitalDocumentEventRestrictedOutValue value = mapper.toValue(document());

        assertThat(value.getPayload().getMetadata()).isNotNull();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private Document document() {
        return Document.builder()
                .id(DOCUMENT_ID)
                .employeeId("E001")
                .managedGroupId("08")
                .status(DocumentStatus.STORED)
                .createdAt(OffsetDateTime.now())
                .build();
    }
}
