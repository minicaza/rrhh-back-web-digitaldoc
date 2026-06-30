package com.mercadona.rrhh.digitaldoc.driven.repositories.models;

import com.mercadona.codehut.jpa.EntityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing the document_error table.
 * Stores the historical record of processing errors for a document.
 */
@Entity
@Table(name = "document_error")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentErrorMO {

    /** Sequential identifier of the error record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** Identifier of the document that failed. */
    @Column(name = "document_id")
    private UUID documentId;

    /** Pipeline step where the failure occurred (e.g. ENRICHMENT, PDF_GENERATION, STORAGE, PUBLICATION). */
    @Column(name = "failed_step")
    private String failedStep;

    /** Detail of the error produced. */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /** Timestamp when the error was recorded — set automatically on insert. */
    @CreationTimestamp
    @Column(name = "error_time")
    private OffsetDateTime errorTime;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, DocumentErrorMO::getId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
