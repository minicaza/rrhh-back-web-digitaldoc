package com.mercadona.rrhh.digitaldoc.application.services.steps;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.OutboxPublicationPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.FailedStep;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPublicationStepTest {

    @Mock private OutboxPublicationPort outboxPort;
    @InjectMocks private KafkaPublicationStep step;

    @Test
    void stepMetadata_isCorrect() {
        assertThat(step.fromStatus()).isEqualTo(DocumentStatus.STORED);
        assertThat(step.toStatus()).isEqualTo(DocumentStatus.PUBLISHED);
        assertThat(step.failedStep()).isEqualTo(FailedStep.PUBLICATION);
    }

    @Test
    void handlesOwnPersistence_isTrue() {
        assertThat(step.handlesOwnPersistence()).isTrue();
    }

    @Test
    void execute_delegatesToOutboxPort() {
        var doc = storedDocument();

        step.execute(doc);

        verify(outboxPort).saveAndMarkPublished(doc);
    }

    @Test
    void execute_propagatesExceptionFromOutbox() {
        var doc = storedDocument();
        doThrow(new RuntimeException("Outbox error")).when(outboxPort).saveAndMarkPublished(any());

        assertThatThrownBy(() -> step.execute(doc))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Outbox error");
    }

    private Document storedDocument() {
        return Document.builder()
                .id(UUID.randomUUID())
                .employeeId("1234567")
                .managedGroupId("08")
                .status(DocumentStatus.STORED)
                .build();
    }
}
