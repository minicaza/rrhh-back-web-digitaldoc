package com.mercadona.rrhh.digitaldoc.domain;

/**
 * Projection that combines a {@link Document} with the localised name of its current status.
 * Used as the return type of query use cases that need to include a localised status name
 * in the response without adding presentation-layer data to the core domain entity.
 *
 * @param document   the document entity
 * @param statusName localised name of the document status
 */
public record DocumentView(Document document, String statusName) {
}
