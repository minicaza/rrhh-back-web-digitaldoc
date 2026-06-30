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
 * JPA entity representing the topic_locale_language catalogue table.
 */
@Entity
@Table(name = "topic_locale_language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocaleLanguageMO {

    /** Locale code (e.g. es-ES, pt-PT). */
    @Id
    @Column(name = "locale")
    private String locale;

    /** Human-readable language name. */
    @Column(name = "name")
    private String name;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, LocaleLanguageMO::getLocale);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
