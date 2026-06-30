package com.mercadona.rrhh.digitaldoc.driven.enrichment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EnrichmentClientConfig {

    @Bean("cardgeneratorWebClient")
    public WebClient cardgeneratorWebClient(
            @Value("${cardgenerator.base-url:http://localhost:8081}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
