package com.olaleyeone.auth.configuration;

import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.api.EntityDataExtractor;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.impl.EntityDataExtractorImpl;
import com.olaleyeone.audittrail.impl.EntityStateLoggerImpl;
import com.olaleyeone.audittrail.impl.UnitOfWorkLogger;
import com.olaleyeone.audittrail.impl.UnitOfWorkLoggerFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Configuration
public class AuditTrailConfiguration {

    @Bean
    public AuditTrailAdvice auditTrailAdvice(EntityDataExtractor entityDataExtractor, Provider<EntityStateLogger> entityStateLoggerProvider) {
        return new AuditTrailAdvice(entityDataExtractor, entityStateLoggerProvider);
    }

    @Bean
    public EntityDataExtractor dataMapExtractor(EntityManager entityManager) {
        return new EntityDataExtractorImpl(entityManager) {
            @Override
            public Class<?> getType(Object e) {
                if (e instanceof HibernateProxy) {
                    return ((HibernateProxy) e).getHibernateLazyInitializer().getPersistentClass();
                }
                return e.getClass();
            }
        };
    }

    @Bean
    public UnitOfWorkLoggerFactory auditTrailLoggerFactory(ApplicationContext applicationContext) {
        return new UnitOfWorkLoggerFactory() {

            @PersistenceContext
            private EntityManager entityManager;

            @Override
            public UnitOfWorkLogger createLogger() {
                return new UnitOfWorkLogger(new EntityStateLoggerImpl(), entityManager) {
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
}
