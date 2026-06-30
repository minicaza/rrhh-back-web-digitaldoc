package com.mercadona.rrhh.digitaldoc.domain;

/**
 * Employee data enriched from the external cardgenerator API.
 * Used during pipeline processing to populate the PDF certificate.
 */
public record EmployeeInfo(
        String employeeId,
        String managedGroupId,
        String fullName,
        String jobFunction,
        String department,
        String email,
        String phoneExtension,
        String location,
        AiCertificationInfo certification
) {}
