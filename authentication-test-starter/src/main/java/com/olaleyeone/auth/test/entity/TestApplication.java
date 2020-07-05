package com.olaleyeone.auth.test.entity;

import com.github.olaleyeone.entitysearch.JpaQuerySource;
import com.olaleyeone.data.dto.RequestMetadata;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.persistence.EntityManager;

@SpringBootApplication
@EnableJpaRepositories({"com.olaleyeone.auth.repository"})
@EntityScan({"com.olaleyeone.data", "com.olaleyeone.auth.data"})
public class TestApplication {

    @Bean
    public RequestMetadata requestMetadata() {
        return Mockito.mock(RequestMetadata.class);
    }

    @Bean
    public JpaQuerySource jpaQuerySource(EntityManager entityManager) {
        return new JpaQuerySource(entityManager);
    }

}
