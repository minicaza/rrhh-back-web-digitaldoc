package com.mercadona.rrhh.digitaldoc.driven.repositories.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA repository configuration for the digitaldoc postgres-repository module.
 * Registers JPA repositories, entity scan paths, and the CNA outbox framework components.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.mercadona.rrhh.digitaldoc.driven.repositories.jpa")
@EntityScan(basePackages = {
        "com.mercadona.rrhh.digitaldoc.driven.repositories.models",
        "com.mercadona.framework.cna.commons.outbox"
})
@ComponentScan(basePackages = {
        "com.mercadona.rrhh.digitaldoc",
        "com.mercadona.framework.cna.commons.outbox.jpa.configuration"
})
public class RepositoryConfig {
}
