package com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk;

import com.mercadona.codehut.jpa.EntityUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for the document_status_names table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentStatusNamesPK implements Serializable {

    /** Document status identifier. */
    @Column(name = "document_status_id")
    private Short documentStatusId;

    /** Locale code (e.g. es-ES). */
    @Column(name = "locale")
    private String locale;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, DocumentStatusNamesPK::getDocumentStatusId, DocumentStatusNamesPK::getLocale);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(documentStatusId, locale);
    }
}
