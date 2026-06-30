package com.mercadona.rrhh.digitaldoc.driven.repositories.mappers;

import com.mercadona.rrhh.digitaldoc.domain.DocumentError;
import com.mercadona.rrhh.digitaldoc.driven.repositories.models.DocumentErrorMO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper between {@link DocumentErrorMO} and {@link DocumentError}.
 */
@Mapper(componentModel = "spring")
public interface DocumentErrorMOMapper {

    /**
     * Converts a {@link DocumentErrorMO} to its domain representation.
     *
     * @param mo the JPA entity
     * @return the domain entity
     */
    DocumentError toDomain(DocumentErrorMO mo);

    /**
     * Converts a {@link DocumentError} to its JPA entity representation.
     *
     * @param error the domain entity
     * @return the JPA entity
     */
    DocumentErrorMO toMO(DocumentError error);

    /**
     * Converts a list of {@link DocumentErrorMO} to a list of domain entities.
     *
     * @param moList the list of JPA entities
     * @return the list of domain entities
     */
    List<DocumentError> toDomainList(List<DocumentErrorMO> moList);
}
