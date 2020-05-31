package com.olaleyeone.auth.servicetest;

import com.olaleyeone.audittrail.advice.EntityManagerAdvice;
import com.olaleyeone.audittrail.api.EntityDataExtractor;
import com.olaleyeone.audittrail.configuration.AuditTrailConfiguration;
import com.olaleyeone.audittrail.impl.TaskTransactionContext;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Provider;
import javax.persistence.EntityManager;

@Configuration
public class TestAuditTrailConfiguration extends AuditTrailConfiguration {

    @Override
    public EntityDataExtractor entityDataExtractor(EntityManager entityManager) {
        return Mockito.mock(EntityDataExtractor.class);
    }

    @Bean
    @Override
    public EntityManagerAdvice entityManagerAdvice(EntityDataExtractor entityDataExtractor, Provider<TaskTransactionContext> taskTransactionContextProvider) {
        return super.entityManagerAdvice(entityDataExtractor, taskTransactionContextProvider);
    }
}
