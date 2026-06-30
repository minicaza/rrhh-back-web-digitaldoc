package com.mercadona.rrhh.digitaldoc.driven.repositories.models;

import com.mercadona.codehut.jpa.EntityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing the document_status catalogue table.
 */
@Entity
@Table(name = "document_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatusMO {

    /** Numeric identifier of the status. */
    @Id
    @Column(name = "id")
    private Short id;

    /** Technical name of the status (e.g. PENDING, ENRICHED). */
    @Column(name = "status")
    private String status;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, DocumentStatusMO::getId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
