package com.mercadona.rrhh.digitaldoc.driven.repositories.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * JPA repository configuration for the digitaldoc postgres-repository module.
 * Registers JPA repositories and entity scan paths.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.mercadona.rrhh.digitaldoc.driven.repositories.jpa")
@EntityScan(basePackages = "com.mercadona.rrhh.digitaldoc.driven.repositories.models")
public class RepositoryConfig {
}
