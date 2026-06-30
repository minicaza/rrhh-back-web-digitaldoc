package com.mercadona.rrhh.digitaldoc.driven.repositories.adapters.stubs;

import com.mercadona.rrhh.digitaldoc.application.ports.driven.OutboxPublicationPort;
import com.mercadona.rrhh.digitaldoc.domain.Document;
import org.springframework.stereotype.Component;

/** Temporary stub — replace with fwkcna-starter-outbox-avro-jpa-register implementation. */
@Component
public class OutboxPublicationStubAdapter implements OutboxPublicationPort {

    @Override
    public void saveAndMarkPublished(Document document) {
        throw new UnsupportedOperationException("OutboxPublicationPort not implemented yet");
    }
}
