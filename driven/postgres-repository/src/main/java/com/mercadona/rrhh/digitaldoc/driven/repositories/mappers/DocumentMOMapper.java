package com.mercadona.rrhh.digitaldoc.driven.repositories.mappers;

import com.mercadona.rrhh.digitaldoc.domain.Document;
import com.mercadona.rrhh.digitaldoc.domain.DocumentStatus;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentMO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper between {@link DocumentMO} and {@link Document}.
 */
@Mapper(componentModel = "spring", imports = {DocumentStatus.class})
public interface DocumentMOMapper {

    /**
     * Converts a {@link DocumentMO} to its domain representation.
     *
     * @param mo the JPA entity
     * @return the domain entity
     */
    @Mapping(target = "status", expression = "java(DocumentStatus.fromId(mo.getDocumentStatusId()))")
    Document toDomain(DocumentMO mo);

    /**
     * Converts a {@link Document} to its JPA entity representation.
     *
     * @param domain the domain entity
     * @return the JPA entity
     */
    @Mapping(target = "documentStatusId", expression = "java(domain.getStatus().getId())")
    DocumentMO toMO(Document domain);

    /**
     * Converts a list of {@link DocumentMO} to a list of domain entities.
     *
     * @param moList the list of JPA entities
     * @return the list of domain entities
     */
    List<Document> toDomainList(List<DocumentMO> moList);
}
