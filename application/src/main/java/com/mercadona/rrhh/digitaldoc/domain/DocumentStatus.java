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

    /**
     * Returns the set of statuses this status can legally transition to,
     * mirroring the document_status_transitions catalogue table.
     */
    public java.util.Set<DocumentStatus> allowedNextStatuses() {
        return switch (this) {
            case PENDING       -> java.util.Set.of(ENRICHED, FAILED);
            case ENRICHED      -> java.util.Set.of(PDF_GENERATED, FAILED);
            case PDF_GENERATED -> java.util.Set.of(STORED, FAILED);
            case STORED        -> java.util.Set.of(PUBLISHED, FAILED);
            case FAILED        -> java.util.Set.of(ENRICHED, PDF_GENERATED, STORED, PUBLISHED);
            case PUBLISHED     -> java.util.Set.of();
        };
    }

    /** Returns {@code true} if transitioning to {@code target} is a valid lifecycle move. */
    public boolean canTransitionTo(DocumentStatus target) {
        return allowedNextStatuses().contains(target);
    }
}
