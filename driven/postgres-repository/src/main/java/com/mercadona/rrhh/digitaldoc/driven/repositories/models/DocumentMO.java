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
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA entity representing the document table.
 * Stores the relationship between an employee and their digital certification document.
 */
@Audited
@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMO {

    /** Unique UUID identifier. The PDF path is derived as documents/{id}.pdf. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /** Employee identifier. */
    @Column(name = "employee_id")
    private String employeeId;

    /** Managed group identifier. */
    @Column(name = "managed_group_id")
    private String managedGroupId;

    /** Current document lifecycle status identifier. */
    @Column(name = "document_status_id")
    private Short documentStatusId;

    /** Timestamp of record creation — set automatically on insert. */
    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    /** Timestamp of the last update — set automatically on update. */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, DocumentMO::getId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
