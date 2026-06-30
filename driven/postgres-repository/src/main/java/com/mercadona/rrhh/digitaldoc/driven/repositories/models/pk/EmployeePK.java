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
 * Composite primary key for the topic_employee table.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeePK implements Serializable {

    /** Employee identifier. */
    @Column(name = "employee_id")
    private String employeeId;

    /** Managed group identifier. */
    @Column(name = "managed_group_id")
    private String managedGroupId;

    @Override
    public final boolean equals(Object object) {
        return EntityUtils.equals(this, object, EmployeePK::getEmployeeId, EmployeePK::getManagedGroupId);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(employeeId, managedGroupId);
    }
}
