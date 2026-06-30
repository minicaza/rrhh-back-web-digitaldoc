package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters.stubs;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.BucketStoragePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Temporary stub — replace with MinIO/S3 implementation. */
@Component
public class BucketStorageStubAdapter implements BucketStoragePort {

    @Override
    public void upload(UUID documentId, byte[] pdfBytes) {
        throw new UnsupportedOperationException("BucketStoragePort not implemented yet");
    }
}
