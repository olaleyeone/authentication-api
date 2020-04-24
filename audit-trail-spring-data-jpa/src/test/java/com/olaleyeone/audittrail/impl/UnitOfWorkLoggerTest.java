package com.olaleyeone.audittrail.impl;

import com.olalayeone.audittrailtest.EntityTest;
import com.olaleyeone.audittrail.api.EntityOperation;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import com.olaleyeone.audittrail.error.NoActivityLogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class UnitOfWorkLoggerTest extends EntityTest {

    private UnitOfWorkLogger unitOfWorkLogger;

    private UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate;
    private EntityStateLogger entityStateLogger;

    private RequestLog requestLog;

    @BeforeEach
    void setUp() {
        unitOfWorkLoggerDelegate = Mockito.mock(UnitOfWorkLoggerDelegate.class);
        entityStateLogger = Mockito.mock(EntityStateLogger.class);
        requestLog = new RequestLog();

        unitOfWorkLogger = new UnitOfWorkLogger(unitOfWorkLoggerDelegate, entityStateLogger) {

            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.of(requestLog);
            }
        };
        unitOfWorkLogger.getActivityLogger().log(faker.funnyName().name(), faker.backToTheFuture().quote());
    }

    @Test
    void shouldIgnoreNoUpdate() {
        Mockito.doReturn(Collections.EMPTY_LIST).when(entityStateLogger).getOperations();
        unitOfWorkLogger.beforeCommit(false);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.never()).saveUnitOfWork(Mockito.any(), Mockito.any());
    }

    @Test
    void shouldSaveUpdates() {
        Mockito.doReturn(Collections.singletonList(Mockito.mock(EntityOperation.class))).when(entityStateLogger).getOperations();
        unitOfWorkLogger.beforeCommit(false);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.times(1)).saveUnitOfWork(unitOfWorkLogger, UnitOfWork.Status.SUCCESSFUL);
    }

    @Test
    void shouldNotSaveErrorAfterCommit() {
        unitOfWorkLogger.afterCompletion(TransactionSynchronization.STATUS_COMMITTED);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.never()).saveFailure(Mockito.any(), Mockito.any());
    }

    @Test
    void shouldNotSaveErrorWhenNoActivityWasDone() {
        UnitOfWorkLogger unitOfWorkLogger = getUnitOfWorkLogger(false);
        unitOfWorkLogger.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.never()).saveFailure(Mockito.any(), Mockito.any());
    }

    @Test
    void shouldSaveErrorForRollback() {
        unitOfWorkLogger.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.times(1))
                .saveFailure(unitOfWorkLogger, UnitOfWork.Status.ROLLED_BACK);
    }

    @Test
    void shouldSaveErrorForUnknown() {
        unitOfWorkLogger.afterCompletion(TransactionSynchronization.STATUS_UNKNOWN);
        Mockito.verify(unitOfWorkLoggerDelegate, Mockito.times(1))
                .saveFailure(unitOfWorkLogger, UnitOfWork.Status.UNKNOWN);
    }

    @Test
    void shouldRequireActivityLogBeforeCommitByDefault() {
        UnitOfWorkLogger unitOfWorkLogger = new UnitOfWorkLogger(null) {
            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.empty();
            }
        };
        assertFalse(unitOfWorkLogger.canCommitWithoutActivityLog());
    }

    @Test
    void shouldRequireActivityLogBeforeCommit() {
        UnitOfWorkLogger unitOfWorkLogger = getUnitOfWorkLogger(false);
        assertThrows(NoActivityLogException.class, () -> unitOfWorkLogger.beforeCommit(false));
    }

    @Test
    void shouldRequireActivityLogBeforeStateUpdate() {
        UnitOfWorkLogger unitOfWorkLogger = getUnitOfWorkLogger(false);
        assertThrows(NoActivityLogException.class, () -> unitOfWorkLogger.getEntityStateLogger());
    }

    @Test
    void shouldNotRequireActivityLog_IfCanCommitWithoutActivityLog() {
        UnitOfWorkLogger unitOfWorkLogger = getUnitOfWorkLogger(true);
        unitOfWorkLogger.beforeCommit(false);
    }

    @Test
    void shouldNotRequireActivityLogBeforeStateUpdate_IfCanCommitWithoutActivityLog() {
        UnitOfWorkLogger unitOfWorkLogger = getUnitOfWorkLogger(true);
        unitOfWorkLogger.getEntityStateLogger();
    }

    private UnitOfWorkLogger getUnitOfWorkLogger(boolean canCommitWithoutActivityLog) {
        return new UnitOfWorkLogger(unitOfWorkLoggerDelegate) {

            @Override
            public Optional<RequestLog> getRequest() {
                return Optional.of(requestLog);
            }

            @Override
            public boolean canCommitWithoutActivityLog() {
                return canCommitWithoutActivityLog;
            }
        };
    }
}