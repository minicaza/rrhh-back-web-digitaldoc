package com.mercadona.rrhh.digitaldoc.domain;

import lombok.Getter;

/**
 * Lifecycle status of a digital certification document.
 * Each value maps to a numeric identifier stored in the document_status catalogue table.
 */
@Getter
public enum DocumentStatus {

    /** Event received and document record created. */
    PENDING((short) 1),

    /** Document enriched with employee data. */
    ENRICHED((short) 2),

    /** PDF successfully generated. */
    PDF_GENERATED((short) 3),

    /** PDF stored in the file system. */
    STORED((short) 4),

    /** Document published and available to the employee. */
    PUBLISHED((short) 5),

    /** Document processing failed. */
    FAILED((short) 6);

    /**
     * -- GETTER --
     *  Returns the numeric identifier of this status as stored in the database.
     *
     * @return the document_status table id
     */
    private final Short id;

    DocumentStatus(Short id) {
        this.id = id;
    }

    /**
     * Resolves a {@link DocumentStatus} from its numeric database identifier.
     *
     * @param id the numeric status id
     * @return the matching status
     * @throws IllegalArgumentException if the id does not match any status
     */
    public static DocumentStatus fromId(Short id) {
        for (DocumentStatus status : values()) {
            if (status.id.equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown document status id: " + id);
    }
}
