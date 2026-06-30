package com.mercadona.rrhh.digitaldoc.driven.bucket.adapters;

import com.google.common.io.ByteSource;
import com.mercadona.framework.cna.lib.bucket.service.BucketService;
import com.mercadona.rrhh.digitaldoc.application.ports.driven.BucketStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Adapter that uploads the generated PDF certificate to an S3-compatible object storage bucket.
 *
 * <p>The storage path is always deterministic: {@code documents/{documentId}.pdf}.
 * This means retries always overwrite the same object — idempotent by design.
 */
@Service
public class BucketStorageAdapter implements BucketStoragePort {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String PATH_TEMPLATE    = "documents/%s.pdf";

    private final BucketService bucketService;
    private final String bucketId;

    public BucketStorageAdapter(BucketService bucketService,
                                @Value("${fwkcna.buckets[0].id}") String bucketId) {
        this.bucketService = bucketService;
        this.bucketId = bucketId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(UUID documentId, byte[] pdfBytes) {
        String path = String.format(PATH_TEMPLATE, documentId);
        try {
            bucketService.upload(bucketId, ByteSource.wrap(pdfBytes), path, PDF_CONTENT_TYPE, Map.of());
        } catch (Exception e) {
            throw new RuntimeException(e); // TODO: Replace with corresponding business exception
        }
    }
}
