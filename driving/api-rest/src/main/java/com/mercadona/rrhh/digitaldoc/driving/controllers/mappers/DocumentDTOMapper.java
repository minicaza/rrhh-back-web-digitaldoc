package com.mercadona.rrhh.digitaldoc.driving.controllers.mappers;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatusInfo;
import com.mercadona.rrhh.digitaldoc.domain.DocumentView;
import com.mercadona.rrhh.digitaldoc.model.DocumentByEmployeeResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentByIdResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentStatusData;
import com.mercadona.rrhh.digitaldoc.model.DocumentStatusResponse;
import com.mercadona.rrhh.digitaldoc.model.DocumentSummaryResponse;
import com.mercadona.rrhh.digitaldoc.model.Employee;
import com.mercadona.rrhh.digitaldoc.model.Pagination;
import com.mercadona.rrhh.digitaldoc.model.SuccessGetDocuments;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper between domain projections and generated API response DTOs.
 */
@Mapper(componentModel = "spring")
public interface DocumentDTOMapper {

    /**
     * Maps domain {@link com.mercadona.rrhh.digitaldoc.domain.DocumentStatus} to the API model enum.
     *
     * @param status the domain status
     * @return the API model status
     */
    com.mercadona.rrhh.digitaldoc.model.DocumentStatus toApiStatus(
        com.mercadona.rrhh.digitaldoc.domain.DocumentStatus status);

    /**
     * Builds a {@link DocumentStatusData} from the status and localised name in the view.
     *
     * @param view the document view
     * @return the status data DTO
     */
    default DocumentStatusData toStatusData(DocumentView view) {
        return new DocumentStatusData(toApiStatus(view.document().getStatus()), view.statusName());
    }

    /**
     * Builds an {@link Employee} DTO from the document in the view.
     *
     * @param view the document view
     * @return the employee DTO
     */
    default Employee toEmployee(DocumentView view) {
        var employee = new Employee();
        employee.setEmployeeId(view.document().getEmployeeId());
        employee.setManagedGroupId(view.document().getManagedGroupId());
        return employee;
    }

    /**
     * Maps a {@link DocumentView} to a {@link DocumentByEmployeeResponse}.
     *
     * @param view the document view
     * @return the response DTO
     */
    default DocumentByEmployeeResponse toDocumentByEmployeeResponse(DocumentView view) {
        return new DocumentByEmployeeResponse(view.document().getId(), toStatusData(view));
    }

    /**
     * Maps a {@link DocumentView} to a {@link DocumentByIdResponse}.
     *
     * @param view the document view
     * @return the response DTO
     */
    default DocumentByIdResponse toDocumentByIdResponse(DocumentView view) {
        return new DocumentByIdResponse(toEmployee(view), toStatusData(view));
    }

    /**
     * Maps a {@link DocumentView} to a {@link DocumentSummaryResponse}.
     *
     * @param view the document view
     * @return the response DTO
     */
    default DocumentSummaryResponse toDocumentSummaryResponse(DocumentView view) {
        return new DocumentSummaryResponse(view.document().getId(), toEmployee(view), toStatusData(view));
    }

    /**
     * Maps a {@link DocumentStatusInfo} to a {@link DocumentStatusResponse}.
     *
     * @param statusInfo the domain status info
     * @return the response DTO
     */
    default DocumentStatusResponse toDocumentStatusResponse(DocumentStatusInfo statusInfo) {
        return new DocumentStatusResponse(toApiStatus(statusInfo.status()), statusInfo.description());
    }

    /**
     * Builds the pagination metadata from a {@link MercadonaPage} and the 1-based requested page.
     *
     * @param page      the result page
     * @param firstPage the 1-based page number requested
     * @return the pagination DTO
     */
    default Pagination toPagination(MercadonaPage<?> page, int firstPage) {
        var pagination = new Pagination();
        pagination.setRequestedPage(firstPage);
        pagination.setRequestedSize(page.getSize());
        pagination.setRetrievedResults(page.getNumberOfElements());
        pagination.setTotalResults(page.getTotalElements());
        return pagination;
    }

    /**
     * Maps a {@link MercadonaPage} of {@link DocumentView} to a {@link SuccessGetDocuments}.
     *
     * @param page      the result page
     * @param firstPage the 1-based page number requested
     * @return the paginated response DTO
     */
    default SuccessGetDocuments toSuccessGetDocuments(MercadonaPage<DocumentView> page, int firstPage) {
        List<DocumentSummaryResponse> data = page.getContent().stream()
            .map(this::toDocumentSummaryResponse)
            .toList();
        return new SuccessGetDocuments(data, toPagination(page, firstPage));
    }
}
