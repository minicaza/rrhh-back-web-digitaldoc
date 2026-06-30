package com.mercadona.rrhh.digitaldoc.application.ports.driven;

import java.util.UUID;

/**
 * Outbound port for uploading the PDF certificate to object storage.
 * The storage path is always deterministic: {@code documents/{documentId}.pdf}.
 */
public interface BucketStoragePort {

    /**
     * Uploads the PDF bytes to the object storage bucket.
     *
     * @param documentId the document UUID used to derive the storage path
     * @param pdfBytes   the PDF content to upload
     */
    void upload(UUID documentId, byte[] pdfBytes);
}
