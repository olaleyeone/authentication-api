package com.olaleyeone.auth.servicetest;

import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.audittrail.api.EntityDataExtractor;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.impl.EntityDataExtractorImpl;
import com.olaleyeone.audittrail.impl.UnitOfWorkLogger;
import com.olaleyeone.audittrail.impl.UnitOfWorkLoggerDelegate;
import com.olaleyeone.audittrail.impl.UnitOfWorkLoggerFactory;
import org.hibernate.proxy.HibernateProxy;
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
    public UnitOfWorkLoggerFactory auditTrailLoggerFactory(ApplicationContext applicationContext) {
        return new UnitOfWorkLoggerFactory() {
            @Override
            public UnitOfWorkLogger createLogger(UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate) {
                return new UnitOfWorkLogger(unitOfWorkLoggerDelegate, Mockito.mock(EntityStateLogger.class)) {
                    @Override
                    public Optional<RequestLog> getRequest() {
                        return Optional.empty();
                    }
                };
            }
        };
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EntityStateLogger entityHistoryLogger(UnitOfWorkLogger unitOfWorkLogger) {
        return unitOfWorkLogger.getEntityStateLogger();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ActivityLogger activityLogger(UnitOfWorkLogger unitOfWorkLogger) {
        return unitOfWorkLogger.getActivityLogger();
    }
}
