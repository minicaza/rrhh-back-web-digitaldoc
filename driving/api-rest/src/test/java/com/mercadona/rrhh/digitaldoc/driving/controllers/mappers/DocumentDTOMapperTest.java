package com.mercadona.rrhh.digitaldoc.driving.controllers.mappers;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatusInfo;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentDTOMapperTest {

    private final DocumentDTOMapper mapper = new DocumentDTOMapperImpl();

    @Mock
    MercadonaPage<DocumentView> page;

    @ParameterizedTest
    @EnumSource(DocumentStatus.class)
    void toApiStatus_mapsAllDomainStatusValues(DocumentStatus domainStatus) {
        var result = mapper.toApiStatus(domainStatus);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(domainStatus.name());
    }

    @Test
    void toStatusData_setsIdAndName() {
        var view = documentView(DocumentStatus.PENDING, "Pendiente");

        var result = mapper.toStatusData(view);

        assertThat(result.getId().name()).isEqualTo("PENDING");
        assertThat(result.getName()).isEqualTo("Pendiente");
    }

    @Test
    void toEmployee_setsEmployeeIdAndManagedGroupId() {
        var view = documentView(DocumentStatus.PUBLISHED, "Publicado");

        var result = mapper.toEmployee(view);

        assertThat(result.getEmployeeId()).isEqualTo("1234567");
        assertThat(result.getManagedGroupId()).isEqualTo("08");
    }

    @Test
    void toDocumentByEmployeeResponse_setsIdAndStatus() {
        var id = UUID.randomUUID();
        var view = documentView(id, DocumentStatus.ENRICHED, "Enriquecido");

        var result = mapper.toDocumentByEmployeeResponse(view);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getStatus().getId().name()).isEqualTo("ENRICHED");
        assertThat(result.getStatus().getName()).isEqualTo("Enriquecido");
    }

    @Test
    void toDocumentByIdResponse_setsEmployeeAndStatus() {
        var view = documentView(DocumentStatus.STORED, "Almacenado");

        var result = mapper.toDocumentByIdResponse(view);

        assertThat(result.getEmployee().getEmployeeId()).isEqualTo("1234567");
        assertThat(result.getEmployee().getManagedGroupId()).isEqualTo("08");
        assertThat(result.getStatus().getId().name()).isEqualTo("STORED");
        assertThat(result.getStatus().getName()).isEqualTo("Almacenado");
    }

    @Test
    void toDocumentSummaryResponse_setsIdEmployeeAndStatus() {
        var id = UUID.randomUUID();
        var view = documentView(id, DocumentStatus.PDF_GENERATED, "PDF generado");

        var result = mapper.toDocumentSummaryResponse(view);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEmployee().getEmployeeId()).isEqualTo("1234567");
        assertThat(result.getStatus().getId().name()).isEqualTo("PDF_GENERATED");
        assertThat(result.getStatus().getName()).isEqualTo("PDF generado");
    }

    @Test
    void toDocumentStatusResponse_setsStatusAndDescription() {
        var statusInfo = new DocumentStatusInfo(DocumentStatus.FAILED, "Fallido");

        var result = mapper.toDocumentStatusResponse(statusInfo);

        assertThat(result.getStatus().name()).isEqualTo("FAILED");
        assertThat(result.getDescription()).isEqualTo("Fallido");
    }

    @Test
    void toPagination_mapsAllFields() {
        when(page.getSize()).thenReturn(20);
        when(page.getNumberOfElements()).thenReturn(15);
        when(page.getTotalElements()).thenReturn(100L);

        var result = mapper.toPagination(page, 3);

        assertThat(result.getRequestedPage()).isEqualTo(3);
        assertThat(result.getRequestedSize()).isEqualTo(20);
        assertThat(result.getRetrievedResults()).isEqualTo(15);
        assertThat(result.getTotalResults()).isEqualTo(100L);
    }

    @Test
    void toSuccessGetDocuments_mapsContentAndPagination() {
        var id = UUID.randomUUID();
        var view = documentView(id, DocumentStatus.PUBLISHED, "Publicado");
        when(page.getContent()).thenReturn(List.of(view));
        when(page.getSize()).thenReturn(10);
        when(page.getNumberOfElements()).thenReturn(1);
        when(page.getTotalElements()).thenReturn(1L);

        var result = mapper.toSuccessGetDocuments(page, 1);

        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getId()).isEqualTo(id);
        assertThat(result.getPagination().getRequestedPage()).isEqualTo(1);
        assertThat(result.getPagination().getRequestedSize()).isEqualTo(10);
        assertThat(result.getPagination().getRetrievedResults()).isEqualTo(1);
        assertThat(result.getPagination().getTotalResults()).isEqualTo(1L);
    }

    @Test
    void toSuccessGetDocuments_mapsMultipleItems() {
        var view1 = documentView(UUID.randomUUID(), DocumentStatus.PUBLISHED, "Publicado");
        var view2 = documentView(UUID.randomUUID(), DocumentStatus.STORED, "Almacenado");
        when(page.getContent()).thenReturn(List.of(view1, view2));
        when(page.getSize()).thenReturn(10);
        when(page.getNumberOfElements()).thenReturn(2);
        when(page.getTotalElements()).thenReturn(2L);

        var result = mapper.toSuccessGetDocuments(page, 1);

        assertThat(result.getData()).hasSize(2);
    }

    private DocumentView documentView(DocumentStatus status, String statusName) {
        return documentView(UUID.randomUUID(), status, statusName);
    }

    private DocumentView documentView(UUID id, DocumentStatus status, String statusName) {
        var doc = Document.builder()
            .id(id)
            .employeeId("1234567")
            .managedGroupId("08")
            .status(status)
            .build();
        return new DocumentView(doc, statusName);
    }
}
