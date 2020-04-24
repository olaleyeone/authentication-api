package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.entity.RequestLog;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@RequiredArgsConstructor
public class UnitOfWorkLoggerFactory implements FactoryBean<UnitOfWorkLogger> {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate;

    @PostConstruct
    public void init() {
        unitOfWorkLoggerDelegate = new UnitOfWorkLoggerDelegate(entityManager, transactionTemplate);
    }

    @Override
    public UnitOfWorkLogger getObject() {
        return (UnitOfWorkLogger) TransactionSynchronizationManager.getSynchronizations()
                .stream()
                .filter(it -> it instanceof UnitOfWorkLogger)
                .findFirst()
                .orElseGet(() -> {
                    UnitOfWorkLogger unitOfWorkLogger = createLogger(entityManager, transactionTemplate);
                    TransactionSynchronizationManager.registerSynchronization(unitOfWorkLogger);
                    return unitOfWorkLogger;
                });
    }

    public UnitOfWorkLogger createLogger(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        return new UnitOfWorkLogger(unitOfWorkLoggerDelegate) {
            @Override
            public Optional<RequestLog> getRequest() {
                return UnitOfWorkLoggerFactory.this.getRequest();
            }
        };
    }

    public Optional<RequestLog> getRequest() {
        return Optional.empty();
    }

    @Override
    public Class<?> getObjectType() {
        return UnitOfWorkLogger.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
