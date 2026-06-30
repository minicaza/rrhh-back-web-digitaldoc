package com.mercadona.rrhh.digitaldoc.driven.repositories.models;

import com.mercadona.codehut.jpa.EntityUtils;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.EmployeePK;
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
 * JPA entity representing the topic_employee catalogue table.
 */
@Entity
@Table(name = "topic_employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeMO {

    /** Composite primary key (employee_id, managed_group_id). */
    @EmbeddedId
    private EmployeePK id;

    /** Employee first name. */
    @Column(name = "name")
    private String name;

    /** Employee first surname. */
    @Column(name = "first_surname")
    private String firstSurname;

    /** Employee second surname. */
    @Column(name = "second_surname")
    private String secondSurname;

    /** Employee preferred name. */
    @Column(name = "favourite_name")
    private String favouriteName;

    /** Whether the employee is currently active. */
    @Column(name = "is_active")
    private Boolean isActive;

    /** Employee professional email address. */
    @Column(name = "professional_email_address")
    private String professionalEmailAddress;

    /** Employee user identifier. */
    @Column(name = "user_id")
    private String userId;

    /** Full computed name (generated always stored column — read-only). */
    @Column(name = "full_name", insertable = false, updatable = false)
    private String fullName;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, EmployeeMO::getId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
