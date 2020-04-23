package com.olaleyeone.audittrail.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
public abstract class UnitOfWorkLoggerFactory implements FactoryBean<UnitOfWorkLogger> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UnitOfWorkLogger getObject() {
        return (UnitOfWorkLogger) TransactionSynchronizationManager.getSynchronizations()
                .stream()
                .filter(it -> it instanceof UnitOfWorkLogger)
                .findFirst()
                .orElseGet(() -> {
                    UnitOfWorkLogger unitOfWorkLogger = createLogger(entityManager);
                    TransactionSynchronizationManager.registerSynchronization(unitOfWorkLogger);
                    return unitOfWorkLogger;
                });
    }

    public abstract UnitOfWorkLogger createLogger(EntityManager entityManager);

    @Override
    public Class<?> getObjectType() {
        return UnitOfWorkLogger.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
