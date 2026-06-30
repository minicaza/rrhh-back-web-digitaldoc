package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.EnrichmentClientPort;
import com.mercadona.rrhh.digitaldoc.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrichmentStepTest {

    @Mock private EnrichmentClientPort enrichmentClient;
    @InjectMocks private EnrichmentStep step;

    @Test
    void stepMetadata_isCorrect() {
        assertThat(step.fromStatus()).isEqualTo(DocumentStatus.PENDING);
        assertThat(step.toStatus()).isEqualTo(DocumentStatus.ENRICHED);
        assertThat(step.failedStep()).isEqualTo(FailedStep.ENRICHMENT);
        assertThat(step.handlesOwnPersistence()).isFalse();
    }

    @Test
    void execute_setsEmployeeInfoOnDocument() {
        var doc = pendingDocument();
        var info = employeeInfo();
        when(enrichmentClient.fetch("1234567", "08")).thenReturn(info);

        step.execute(doc);

        assertThat(doc.getEmployeeInfo()).isEqualTo(info);
        verify(enrichmentClient).fetch("1234567", "08");
    }

    @Test
    void execute_propagatesExceptionFromClient() {
        var doc = pendingDocument();
        when(enrichmentClient.fetch(any(), any())).thenThrow(new RuntimeException("API error"));

        assertThatThrownBy(() -> step.execute(doc))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("API error");
    }

    private Document pendingDocument() {
        return Document.builder()
                .id(UUID.randomUUID())
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.PENDING)
                .build();
    }

    private EmployeeInfo employeeInfo() {
        var cert = new AiCertificationInfo("cert-1", "ACTIVE", true,
                LocalDate.of(2025, 1, 1), LocalDate.of(2026, 1, 1), LocalDate.of(2025, 1, 1),
                "BASIC", List.of("ChatGPT"), "RRHH", "Uso responsable de IA");
        return new EmployeeInfo("1234567", "08", "Ana García", "Desarrolladora",
                "IT", "ana@mercadona.com", "1234", "Valencia", cert);
    }
}
