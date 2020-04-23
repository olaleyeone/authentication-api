package com.olaleyeone.audittrail.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
public abstract class UnitOfWorkLoggerFactory implements FactoryBean<UnitOfWorkLogger> {

    @Override
    public UnitOfWorkLogger getObject() {
        return (UnitOfWorkLogger) TransactionSynchronizationManager.getSynchronizations()
                .stream()
                .filter(it -> it instanceof UnitOfWorkLogger)
                .findFirst()
                .orElseGet(() -> {
                    UnitOfWorkLogger unitOfWorkLogger = createLogger();
                    TransactionSynchronizationManager.registerSynchronization(unitOfWorkLogger);
                    return unitOfWorkLogger;
                });
    }

    public abstract UnitOfWorkLogger createLogger();

    @Override
    public Class<?> getObjectType() {
        return UnitOfWorkLogger.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
