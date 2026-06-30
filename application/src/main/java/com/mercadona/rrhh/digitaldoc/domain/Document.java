package com.mercadona.rrhh.digitaldoc.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain entity representing a digital certification document for an employee.
 * The PDF path is derived as {@code documents/{id}.pdf}.
 *
 * <p>{@code employeeInfo} and {@code pdfBytes} are pipeline-only fields: populated
 * during async processing and never persisted to the database.
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

    /** Current lifecycle status. Updated in-memory by the pipeline orchestrator. */
    @Setter
    private DocumentStatus status;

    /** Timestamp of record creation. */
    private OffsetDateTime createdAt;

    /** Timestamp of the last status update. */
    private OffsetDateTime updatedAt;

    // ── Pipeline-only transient fields (never persisted) ──────────────────────

    /** Employee data fetched by EnrichmentStep. Consumed by PdfGenerationStep. */
    @Setter
    private EmployeeInfo employeeInfo;

    /** PDF bytes produced by PdfGenerationStep. Consumed by BucketStorageStep. */
    @Setter
    private byte[] pdfBytes;
}
