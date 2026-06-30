package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters;

import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.jpa.DocumentStatusNamesMOJpaRepository;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentStatusNamesMO;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.pk.DocumentStatusNamesPK;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentStatusRepositoryAdapterTest {

    @Mock DocumentStatusNamesMOJpaRepository jpaRepository;
    @InjectMocks DocumentStatusRepositoryAdapter adapter;

    @Test
    void findStatusDescription_returnsDescription_whenFound() {
        DocumentStatusNamesPK pk = new DocumentStatusNamesPK(DocumentStatus.PUBLISHED.getId(), "es-ES");
        DocumentStatusNamesMO mo = DocumentStatusNamesMO.builder()
                .id(pk)
                .description("El documento ha sido publicado.")
                .build();
        when(jpaRepository.findById(pk)).thenReturn(Optional.of(mo));

        Optional<String> result = adapter.findStatusDescription(DocumentStatus.PUBLISHED, "es-ES");

        assertThat(result).contains("El documento ha sido publicado.");
    }

    @Test
    void findStatusDescription_returnsEmpty_whenNotFound() {
        DocumentStatusNamesPK pk = new DocumentStatusNamesPK(DocumentStatus.FAILED.getId(), "pt-PT");
        when(jpaRepository.findById(pk)).thenReturn(Optional.empty());

        assertThat(adapter.findStatusDescription(DocumentStatus.FAILED, "pt-PT")).isEmpty();
    }

    @Test
    void findStatusDescription_buildsCorrectPkFromStatus() {
        DocumentStatusNamesPK pk = new DocumentStatusNamesPK(DocumentStatus.PENDING.getId(), "es-ES");
        when(jpaRepository.findById(pk)).thenReturn(Optional.empty());

        adapter.findStatusDescription(DocumentStatus.PENDING, "es-ES");

        // Verifies the PK is built with the numeric id from the enum, not the ordinal
        assertThat(pk.getDocumentStatusId()).isEqualTo((short) 1);
    }
}
