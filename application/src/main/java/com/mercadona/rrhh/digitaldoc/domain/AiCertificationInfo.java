package com.mercadona.rrhh.digitaldoc.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * AI certification data obtained from the external cardgenerator API.
 */
public record AiCertificationInfo(
        String certificationId,
        String status,
        boolean valid,
        LocalDate startDate,
        LocalDate expirationDate,
        LocalDate issuedDate,
        String level,
        List<String> approvedTools,
        String issuedBy,
        String description
) {}
