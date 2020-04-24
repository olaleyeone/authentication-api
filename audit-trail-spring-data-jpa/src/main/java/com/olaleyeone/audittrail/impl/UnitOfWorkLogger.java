package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.audittrail.api.EntityOperation;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.ActivityLog;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import com.olaleyeone.audittrail.error.NoActivityLogException;
import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public abstract class UnitOfWorkLogger implements TransactionSynchronization {

    @Getter(AccessLevel.NONE)
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate;
    private final EntityStateLogger entityStateLogger;
    private final ActivityLogger activityLogger;

    private final List<ActivityLog> activityLogs = new ArrayList<>();
    private final LocalDateTime startTime = LocalDateTime.now();

    public UnitOfWorkLogger(UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate) {
        this(unitOfWorkLoggerDelegate, new EntityStateLoggerImpl());
    }

    public UnitOfWorkLogger(UnitOfWorkLoggerDelegate unitOfWorkLoggerDelegate, EntityStateLogger entityStateLogger) {
        this.unitOfWorkLoggerDelegate = unitOfWorkLoggerDelegate;
        this.entityStateLogger = entityStateLogger;
        this.activityLogger = createActivityLogger(activityLogs);
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        checkHasActivityLog();
        List<EntityOperation> logs = entityStateLogger.getOperations();
        if (logs.isEmpty()) {
            logger.warn("No work done");
            return;
        }
        unitOfWorkLoggerDelegate.saveUnitOfWork(this, UnitOfWork.Status.SUCCESSFUL);
    }

    public abstract Optional<RequestLog> getRequest();

    public ActivityLogger createActivityLogger(List<ActivityLog> activityLogs) {
        return new ActivityLoggerImpl(activityLogs);
    }

    public boolean canCommitWithoutActivityLog() {
        return false;
    }

    public EntityStateLogger getEntityStateLogger() {
        checkHasActivityLog();
        return entityStateLogger;
    }

    @Override
    public void afterCompletion(int status) {
        if (status == TransactionSynchronization.STATUS_COMMITTED || activityLogs.isEmpty()) {
            return;
        }
        unitOfWorkLoggerDelegate.saveFailure(this, status == TransactionSynchronization.STATUS_ROLLED_BACK
                ? UnitOfWork.Status.ROLLED_BACK
                : UnitOfWork.Status.UNKNOWN);
    }

    private void checkHasActivityLog() {
        if (!activityLogs.isEmpty()) {
            return;
        }
        if (!canCommitWithoutActivityLog()) {
            throw new NoActivityLogException();
        }
        logger.warn("No activity log");
    }
}
