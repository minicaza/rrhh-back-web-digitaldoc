package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import com.mercadona.rrhh.digitaldoc.domain.EmployeeInfo;

/**
 * Outbound port for fetching employee enrichment data from the external cardgenerator API.
 */
public interface EnrichmentClientPort {

    /**
     * Fetches AI certification and employee data for a given employee.
     * Results are cached by the adapter implementation.
     *
     * @param employeeId     the employee identifier
     * @param managedGroupId the managed group identifier
     * @return the enriched employee data
     */
    EmployeeInfo fetch(String employeeId, String managedGroupId);
}
