package com.mercadona.rrhh.digitaldoc.driven.enrichment.adapter;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.EnrichmentClientPort;
import com.mercadona.rrhh.digitaldoc.domain.AiCertificationInfo;
import com.mercadona.rrhh.digitaldoc.domain.EmployeeInfo;
import com.mercadona.rrhh.digitaldoc.driven.enrichment.dto.AiCertificationData;
import com.mercadona.rrhh.digitaldoc.driven.enrichment.dto.EmployeeCertificationResponse;
import com.mercadona.rrhh.digitaldoc.driven.enrichment.dto.EmployeeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Adapter that calls the external cardgenerator API to fetch employee AI-certification data.
 *
 * <p>Results are cached in Caffeine (cache name: {@code enrichment}, TTL 10 min, max 10 000 entries).
 * Cache key: {@code employeeId|managedGroupId}. Null results and 4xx errors are never cached.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnrichmentClientAdapter implements EnrichmentClientPort {

    @Qualifier("cardgeneratorWebClient")
    private final WebClient webClient;

    @Override
    @Cacheable(value = "enrichment", key = "#employeeId + '|' + #managedGroupId", unless = "#result == null")
    public EmployeeInfo fetch(String employeeId, String managedGroupId) {
        log.debug("Fetching AI certification for employee {} / group {}", employeeId, managedGroupId);

        EmployeeCertificationResponse response = webClient.get()
                .uri("/managed-groups/{managedGroupId}/employees/{employeeId}/ai-certification",
                        managedGroupId, employeeId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException(
                                        "Enrichment API returned " + clientResponse.statusCode() +
                                        " for employee " + employeeId + "/" + managedGroupId +
                                        ": " + body))))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException(
                                "Enrichment API server error " + clientResponse.statusCode() +
                                " for employee " + employeeId + "/" + managedGroupId)))
                .bodyToMono(EmployeeCertificationResponse.class)
                .block();

        if (response == null || response.getData() == null) {
            return null;
        }
        return toEmployeeInfo(response.getData());
    }

    private EmployeeInfo toEmployeeInfo(EmployeeData data) {
        return new EmployeeInfo(
                data.getEmployeeId(),
                data.getManagedGroupId(),
                data.getFullName(),
                data.getJobFunction(),
                data.getDepartment(),
                data.getEmail(),
                data.getPhoneExtension(),
                data.getLocation(),
                toAiCertificationInfo(data.getCertification())
        );
    }

    private AiCertificationInfo toAiCertificationInfo(AiCertificationData cert) {
        if (cert == null) {
            return null;
        }
        return new AiCertificationInfo(
                cert.getCertificationId(),
                cert.getStatus(),
                cert.isValid(),
                cert.getStartDate(),
                cert.getExpirationDate(),
                cert.getIssuedDate(),
                cert.getLevel(),
                cert.getApprovedTools() != null ? cert.getApprovedTools() : List.of(),
                cert.getIssuedBy(),
                cert.getDescription()
        );
    }
}
