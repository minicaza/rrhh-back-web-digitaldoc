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
 * JPA entity representing the topic_managed_group catalogue table.
 */
@Entity
@Table(name = "topic_managed_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ManagedGroupMO {

    /** Managed group identifier (2-char code). */
    @Id
    @Column(name = "managed_group_id")
    private String managedGroupId;

    /** Financial company public identifier. */
    @Column(name = "financial_company_public_id")
    private String financialCompanyPublicId;

    /** Full name of the managed group. */
    @Column(name = "name")
    private String name;

    /** Short name of the managed group. */
    @Column(name = "short_name")
    private String shortName;

    /** Whether the managed group is visible. */
    @Column(name = "is_visible")
    private Boolean isVisible;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, ManagedGroupMO::getManagedGroupId);
    }

    @Override
    public final int hashCode() {
        return EntityUtils.hashCode(this);
    }
}
