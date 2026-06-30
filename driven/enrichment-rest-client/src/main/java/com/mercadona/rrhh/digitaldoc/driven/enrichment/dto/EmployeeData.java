package com.mercadona.rrhh.digitaldoc.driven.enrichment.dto;

import lombok.Data;

@Data
public class EmployeeData {
    private String employeeId;
    private String managedGroupId;
    private String fullName;
    private String jobFunction;
    private String department;
    private String email;
    private String phoneExtension;
    private String location;
    private AiCertificationData certification;
}
