package com.mercadona.rrhh.digitaldoc.domain;

/**
 * Value object carrying a document status together with its localised human-readable description.
 *
 * @param status      the document lifecycle status
 * @param description localised description of the status
 */
public record DocumentStatusInfo(DocumentStatus status, String description) {
}
