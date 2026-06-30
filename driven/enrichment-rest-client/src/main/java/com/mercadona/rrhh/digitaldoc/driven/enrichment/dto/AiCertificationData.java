package com.mercadona.rrhh.digitaldoc.driven.enrichment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AiCertificationData {
    private String certificationId;
    private String status;
    @JsonProperty("isValid")
    private boolean valid;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private LocalDate issuedDate;
    private String level;
    private List<String> approvedTools;
    private String issuedBy;
    private String description;
}
