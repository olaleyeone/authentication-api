package com.olaleyeone.audittrail.impl;

import com.olalayeone.audittrailtest.EntityTest;
import com.olaleyeone.audittrail.entity.RequestLog;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Provider;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UnitOfWorkLoggerFactoryTest extends EntityTest {

    @Autowired
    private Provider<UnitOfWorkLogger> auditTrailLoggerProvider;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void beforeCommit() {
        UnitOfWorkLogger unitOfWorkLogger = transactionTemplate.execute(status -> auditTrailLoggerProvider.get());
        Mockito.verify(unitOfWorkLogger, Mockito.times(1))
                .beforeCommit(Mockito.anyBoolean());
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Test
    void shouldCreateNewInstanceForEachTransaction() {
        UnitOfWorkLogger unitOfWorkLogger1 = transactionTemplate.execute(status -> auditTrailLoggerProvider.get());
        UnitOfWorkLogger unitOfWorkLogger2 = transactionTemplate.execute(status -> auditTrailLoggerProvider.get());
        assertNotSame(unitOfWorkLogger1, unitOfWorkLogger2);
    }

    @Transactional
    @Test
    void shouldUseOneInstancePerTransaction() {
        UnitOfWorkLogger unitOfWorkLogger1 = auditTrailLoggerProvider.get();
        UnitOfWorkLogger unitOfWorkLogger2 = transactionTemplate.execute(status -> auditTrailLoggerProvider.get());
        assertSame(unitOfWorkLogger1, unitOfWorkLogger2);
    }

    @Test
    void testCreateLogger2() {
        RequestLog requestLog = Mockito.mock(RequestLog.class);
        UnitOfWorkLoggerFactory unitOfWorkLoggerFactory = new UnitOfWorkLoggerFactory() {
            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.of(requestLog);
            }
        };
        UnitOfWorkLogger unitOfWorkLogger = unitOfWorkLoggerFactory.createLogger(null);
        assertEquals(requestLog, unitOfWorkLogger.getRequest().get());
    }
}