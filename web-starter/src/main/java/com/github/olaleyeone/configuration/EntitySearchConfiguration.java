package com.github.olaleyeone.configuration;

import com.github.olaleyeone.entitysearch.JpaQuerySource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class EntitySearchConfiguration {

    @Bean
    public JpaQuerySource jpaQuerySource(EntityManager entityManager) {
        return new JpaQuerySource(entityManager);
    }
}
