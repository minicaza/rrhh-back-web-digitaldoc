package com.mercadona.rrhh.digitaldoc.driven.repositories.mappers;

import com.mercadona.rrhh.digitaldoc.domain.Document;
import org.springframework.stereotype.Component;
import thirdparty.employee.employeedigitaldocument.v0.DataRecord;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocument;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutKey;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentEventRestrictedOutValue;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentStatusIds;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeDigitalDocumentsRecord;
import thirdparty.employee.employeedigitaldocument.v0.EmployeeIds;
import thirdparty.employee.employeedigitaldocument.v0.ManagedGroupIds;
import thirdparty.employee.employeedigitaldocument.v0.MetadataRecord;

import java.util.List;

/**
 * Maps a {@link Document} to the Avro key and value types required by the
 * {@code concepto.thirdparty.employee.employeedigitaldocument} Kafka topic.
 */
@Component
public class EmployeeDigitalDocumentOutboxMapper {

    /**
     * Builds the Avro key from the document's employee and managed group identifiers.
     *
     * @param document the domain document
     * @return the Avro event key
     */
    public EmployeeDigitalDocumentEventRestrictedOutKey toKey(Document document) {
        return EmployeeDigitalDocumentEventRestrictedOutKey.newBuilder()
                .setEmployeeId(document.getEmployeeId())
                .setManagedGroupId(document.getManagedGroupId())
                .build();
    }

    /**
     * Builds the Avro value wrapping all document data in the event payload.
     *
     * @param document the domain document
     * @return the Avro event value
     */
    public EmployeeDigitalDocumentEventRestrictedOutValue toValue(Document document) {
        return EmployeeDigitalDocumentEventRestrictedOutValue.newBuilder()
                .setPayload(buildPayload(document))
                .build();
    }

    private EmployeeDigitalDocument buildPayload(Document document) {
        return EmployeeDigitalDocument.newBuilder()
                .setMetadata(MetadataRecord.newBuilder().build())
                .setData(buildData(document))
                .build();
    }

    private DataRecord buildData(Document document) {
        return DataRecord.newBuilder()
                .setEmployeeData(buildEmployeeIds(document))
                .setEmployeeDigitalDocuments(List.of(buildDocumentRecord(document)))
                .build();
    }

    private EmployeeIds buildEmployeeIds(Document document) {
        return EmployeeIds.newBuilder()
                .setId(document.getEmployeeId())
                .setManagedGroupId(ManagedGroupIds.newBuilder()
                        .setId(document.getManagedGroupId())
                        .build())
                .build();
    }

    private EmployeeDigitalDocumentsRecord buildDocumentRecord(Document document) {
        return EmployeeDigitalDocumentsRecord.newBuilder()
                .setId(document.getId().toString())
                .setCreationDate(document.getCreatedAt() != null ? document.getCreatedAt().toString() : null)
                .setDigitalDocumentStatus(EmployeeDigitalDocumentStatusIds.newBuilder()
                        .setId("PUBLISHED")
                        .build())
                .setDigitalDocumentClass(null)
                .setDigitalDocumentSubclass(null)
                .setDigitalDocumentSubtype(null)
                .build();
    }
}
