package com.mercadona.rrhh.digitaldoc.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentStatusTest {

    @ParameterizedTest
    @CsvSource({"1,PENDING","2,ENRICHED","3,PDF_GENERATED","4,STORED","5,PUBLISHED","6,FAILED"})
    void fromId_returnsCorrectStatus(short id, DocumentStatus expected) {
        assertThat(DocumentStatus.fromId(id)).isEqualTo(expected);
    }

    @Test
    void fromId_unknownId_throwsIllegalArgument() {
        assertThatThrownBy(() -> DocumentStatus.fromId((short) 99))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @ParameterizedTest
    @CsvSource({
            "PENDING,ENRICHED,true",
            "PENDING,FAILED,true",
            "PENDING,PUBLISHED,false",
            "ENRICHED,PDF_GENERATED,true",
            "ENRICHED,FAILED,true",
            "ENRICHED,PENDING,false",
            "PDF_GENERATED,STORED,true",
            "PDF_GENERATED,FAILED,true",
            "PDF_GENERATED,PENDING,false",
            "STORED,PUBLISHED,true",
            "STORED,FAILED,true",
            "STORED,PENDING,false",
            "FAILED,ENRICHED,true",
            "FAILED,PDF_GENERATED,true",
            "FAILED,STORED,true",
            "FAILED,PUBLISHED,true",
            "PUBLISHED,PENDING,false",
            "PUBLISHED,FAILED,false"
    })
    void canTransitionTo_respectsAllowedTransitions(DocumentStatus from, DocumentStatus to, boolean expected) {
        assertThat(from.canTransitionTo(to)).isEqualTo(expected);
    }

    @Test
    void published_hasNoAllowedNextStatuses() {
        assertThat(DocumentStatus.PUBLISHED.allowedNextStatuses()).isEmpty();
    }
}
