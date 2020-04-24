package com.olaleyeone.auth.servicetest;

import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.audittrail.api.EntityDataExtractor;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.Task;
import com.olaleyeone.audittrail.impl.AuditTrailLogger;
import com.olaleyeone.audittrail.impl.AuditTrailLoggerDelegate;
import com.olaleyeone.audittrail.impl.AuditTrailLoggerFactory;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import java.util.Optional;

@Configuration
public class TestAuditTrailConfiguration {

    @Bean
    public AuditTrailAdvice auditTrailAdvice(EntityDataExtractor entityDataExtractor, Provider<EntityStateLogger> entityStateLoggerProvider) {
        return new AuditTrailAdvice(entityDataExtractor, entityStateLoggerProvider);
    }

    @Bean
    public EntityDataExtractor dataMapExtractor(EntityManager entityManager) {
        return Mockito.mock(EntityDataExtractor.class);
    }

    @Bean
    public AuditTrailLoggerFactory auditTrailLoggerFactory(ApplicationContext applicationContext) {
        return new AuditTrailLoggerFactory() {
            @Override
            public AuditTrailLogger createLogger(AuditTrailLoggerDelegate auditTrailLoggerDelegate) {
                return new AuditTrailLogger(auditTrailLoggerDelegate, Mockito.mock(EntityStateLogger.class)) {
                    @Override
                    public Optional<Task> getTask() {
                        return Optional.empty();
                    }
                };
            }
        };
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EntityStateLogger entityHistoryLogger(AuditTrailLogger auditTrailLogger) {
        return auditTrailLogger.getEntityStateLogger();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ActivityLogger activityLogger(AuditTrailLogger auditTrailLogger) {
        return auditTrailLogger.getActivityLogger();
    }
}
