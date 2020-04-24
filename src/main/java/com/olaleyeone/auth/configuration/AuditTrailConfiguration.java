package com.olaleyeone.auth.configuration;

import com.olaleyeone.audittrail.advice.AuditTrailAdvice;
import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.audittrail.api.EntityDataExtractor;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.impl.EntityDataExtractorImpl;
import com.olaleyeone.audittrail.impl.AuditTrailLogger;
import com.olaleyeone.audittrail.impl.AuditTrailLoggerFactory;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.inject.Provider;
import javax.persistence.EntityManager;
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
    public AuditTrailLoggerFactory auditTrailLoggerFactory(ApplicationContext applicationContext) {
        return new AuditTrailLoggerFactory() {
            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.empty();
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
