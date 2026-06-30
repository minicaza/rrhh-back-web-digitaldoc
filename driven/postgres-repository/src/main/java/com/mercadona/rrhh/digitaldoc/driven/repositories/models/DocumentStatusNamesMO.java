package com.mercadona.rrhh.digitaldoc.driven.repositories.models;

import com.mercadona.codehut.jpa.EntityUtils;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.DocumentStatusNamesPK;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing the document_status_names i18n table.
 */
@Entity
@Table(name = "document_status_names")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatusNamesMO {

    /** Composite primary key (document_status_id, locale). */
    @EmbeddedId
    private DocumentStatusNamesPK id;

    /** Localised description of the status. */
    @Column(name = "description")
    private String description;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, DocumentStatusNamesMO::getId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
