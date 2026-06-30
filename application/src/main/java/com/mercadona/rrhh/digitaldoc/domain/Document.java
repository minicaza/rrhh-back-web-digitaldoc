package com.mercadona.rrhh.digitaldoc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain entity representing a digital certification document for an employee.
 * The PDF path is derived as {@code documents/{id}.pdf}.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    /** Unique document identifier. */
    private UUID id;

    /** Employee identifier. */
    private String employeeId;

    /** Managed group identifier. */
    private String managedGroupId;

    /** Current lifecycle status of the document. */
    private DocumentStatus status;

    /** Timestamp of record creation. */
    private OffsetDateTime createdAt;

    /** Timestamp of the last status update. */
    private OffsetDateTime updatedAt;
}
