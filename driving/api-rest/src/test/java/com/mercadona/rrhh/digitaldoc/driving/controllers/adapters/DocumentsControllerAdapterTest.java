package com.mercadona.rrhh.digitaldoc.driving.controllers.adapters;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.application.ports.driving.DocumentQueryPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatusInfo;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;
import com.mercadona.rrhh.digitaldoc.driving.controllers.mappers.DocumentDTOMapper;
import com.mercadona.rrhh.digitaldoc.model.DocumentByEmployeeResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentByIdResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentStatusResponse;
import com.mercadona.rrhh.digitaldoc.model.SuccessGetDocuments;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentsControllerAdapterTest {

    @Mock
    DocumentQueryPort documentQueryPort;

    @Mock
    DocumentDTOMapper mapper;

    @InjectMocks
    DocumentsControllerAdapter adapter;

    // --- getDocumentByEmployee ---

    @Test
    void getDocumentByEmployee_returnsOk_whenDocumentFound() {
        var view = documentView();
        var response = new DocumentByEmployeeResponse();
        when(documentQueryPort.findByEmployee("1234567", "08", "es-ES")).thenReturn(Optional.of(view));
        when(mapper.toDocumentByEmployeeResponse(view)).thenReturn(response);

        var result = adapter.getDocumentByEmployee("1234567", "08", "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void getDocumentByEmployee_returnsNotFound_whenDocumentAbsent() {
        when(documentQueryPort.findByEmployee("1234567", "08", "es-ES")).thenReturn(Optional.empty());

        var result = adapter.getDocumentByEmployee("1234567", "08", "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getDocumentByEmployee_usesDefaultLocale_whenLocaleIsNull() {
        when(documentQueryPort.findByEmployee("1234567", "08", "es-ES")).thenReturn(Optional.empty());

        adapter.getDocumentByEmployee("1234567", "08", null);

        verify(documentQueryPort).findByEmployee("1234567", "08", "es-ES");
    }

    @Test
    void getDocumentByEmployee_usesProvidedLocale_whenLocaleIsNotNull() {
        when(documentQueryPort.findByEmployee("1234567", "08", "en-US")).thenReturn(Optional.empty());

        adapter.getDocumentByEmployee("1234567", "08", "en-US");

        verify(documentQueryPort).findByEmployee("1234567", "08", "en-US");
    }

    // --- getDocumentById ---

    @Test
    void getDocumentById_returnsOk_whenDocumentFound() {
        var id = UUID.randomUUID();
        var view = documentView();
        var response = new DocumentByIdResponse();
        when(documentQueryPort.findById(id, "es-ES")).thenReturn(Optional.of(view));
        when(mapper.toDocumentByIdResponse(view)).thenReturn(response);

        var result = adapter.getDocumentById(id, "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void getDocumentById_returnsNotFound_whenDocumentAbsent() {
        var id = UUID.randomUUID();
        when(documentQueryPort.findById(id, "es-ES")).thenReturn(Optional.empty());

        var result = adapter.getDocumentById(id, "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getDocumentById_usesDefaultLocale_whenLocaleIsNull() {
        var id = UUID.randomUUID();
        when(documentQueryPort.findById(id, "es-ES")).thenReturn(Optional.empty());

        adapter.getDocumentById(id, null);

        verify(documentQueryPort).findById(id, "es-ES");
    }

    // --- getDocumentStatus ---

    @Test
    void getDocumentStatus_returnsOk_whenStatusFound() {
        var id = UUID.randomUUID();
        var statusInfo = new DocumentStatusInfo(DocumentStatus.PUBLISHED, "Publicado");
        var response = new DocumentStatusResponse();
        when(documentQueryPort.getDocumentStatus(id, "es-ES")).thenReturn(Optional.of(statusInfo));
        when(mapper.toDocumentStatusResponse(statusInfo)).thenReturn(response);

        var result = adapter.getDocumentStatus(id, "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void getDocumentStatus_returnsNotFound_whenStatusAbsent() {
        var id = UUID.randomUUID();
        when(documentQueryPort.getDocumentStatus(id, "es-ES")).thenReturn(Optional.empty());

        var result = adapter.getDocumentStatus(id, "es-ES");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getDocumentStatus_usesDefaultLocale_whenLocaleIsNull() {
        var id = UUID.randomUUID();
        when(documentQueryPort.getDocumentStatus(id, "es-ES")).thenReturn(Optional.empty());

        adapter.getDocumentStatus(id, null);

        verify(documentQueryPort).getDocumentStatus(id, "es-ES");
    }

    // --- listDocumentsByStatus ---

    @Test
    void listDocumentsByStatus_returnsOk_withMappedPage() {
        var apiStatus = com.mercadona.rrhh.digitaldoc.model.DocumentStatus.PENDING;
        var domainStatus = DocumentStatus.PENDING;
        @SuppressWarnings("unchecked")
        MercadonaPage<DocumentView> page = mock(MercadonaPage.class);
        var response = new SuccessGetDocuments();
        when(documentQueryPort.listByStatus(domainStatus, "es-ES", 1, 10, "createdAt", "desc")).thenReturn(page);
        when(mapper.toSuccessGetDocuments(page, 1)).thenReturn(response);

        var result = adapter.listDocumentsByStatus(apiStatus, "es-ES", 1, 10, "createdAt", "desc");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isSameAs(response);
    }

    @Test
    void listDocumentsByStatus_convertsDomainStatus() {
        var apiStatus = com.mercadona.rrhh.digitaldoc.model.DocumentStatus.PUBLISHED;
        @SuppressWarnings("unchecked")
        MercadonaPage<DocumentView> page = mock(MercadonaPage.class);
        when(documentQueryPort.listByStatus(DocumentStatus.PUBLISHED, "es-ES", 1, 10, "createdAt", "asc")).thenReturn(page);
        when(mapper.toSuccessGetDocuments(page, 1)).thenReturn(new SuccessGetDocuments());

        adapter.listDocumentsByStatus(apiStatus, "es-ES", 1, 10, "createdAt", "asc");

        verify(documentQueryPort).listByStatus(DocumentStatus.PUBLISHED, "es-ES", 1, 10, "createdAt", "asc");
    }

    @Test
    void listDocumentsByStatus_usesDefaultLocale_whenLocaleIsNull() {
        var apiStatus = com.mercadona.rrhh.digitaldoc.model.DocumentStatus.PENDING;
        @SuppressWarnings("unchecked")
        MercadonaPage<DocumentView> page = mock(MercadonaPage.class);
        when(documentQueryPort.listByStatus(DocumentStatus.PENDING, "es-ES", 1, 10, "createdAt", "desc")).thenReturn(page);
        when(mapper.toSuccessGetDocuments(page, 1)).thenReturn(new SuccessGetDocuments());

        adapter.listDocumentsByStatus(apiStatus, null, 1, 10, "createdAt", "desc");

        verify(documentQueryPort).listByStatus(DocumentStatus.PENDING, "es-ES", 1, 10, "createdAt", "desc");
    }

    private DocumentView documentView() {
        var doc = Document.builder()
            .id(UUID.randomUUID())
            .employeeId("1234567")
            .managedGroupId("08")
            .status(DocumentStatus.PENDING)
            .build();
        return new DocumentView(doc, "Pendiente");
    }
}
