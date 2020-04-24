package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.EntityAttributeData;
import com.olaleyeone.audittrail.api.EntityOperation;
import com.olaleyeone.audittrail.entity.ActivityLog;
import com.olaleyeone.audittrail.entity.EntityState;
import com.olaleyeone.audittrail.entity.EntityStateAttribute;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class UnitOfWorkLoggerDelegate {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    public UnitOfWork saveUnitOfWork(UnitOfWorkLogger unitOfWorkLogger, UnitOfWork.Status status) {
        UnitOfWork unitOfWork = createUnitOfWork(unitOfWorkLogger, status);
        unitOfWorkLogger.getEntityStateLogger().getOperations().forEach(entityHistoryLog -> createEntityHistory(unitOfWork, entityHistoryLog));
        unitOfWorkLogger.getActivityLogs().forEach(activityLog -> {
            activityLog.setId(null);
            activityLog.setUnitOfWork(unitOfWork);
            entityManager.persist(activityLog);
        });
        return unitOfWork;
    }

    UnitOfWork createUnitOfWork(UnitOfWorkLogger unitOfWorkLogger, UnitOfWork.Status status) {
        UnitOfWork unitOfWork = new UnitOfWork();
        unitOfWork.setStatus(status);
        List<ActivityLog> activityLogs = unitOfWorkLogger.getActivityLogs();
        if (!activityLogs.isEmpty()) {
            unitOfWork.setName(activityLogs.iterator().next().getName());
        }
        unitOfWork.setStartedOn(unitOfWorkLogger.getStartTime());
        unitOfWork.setEstimatedTimeTakenInNanos(unitOfWorkLogger.getStartTime().until(LocalDateTime.now(), ChronoUnit.NANOS));
        unitOfWorkLogger.getRequest().ifPresent(unitOfWork::setRequest);
        entityManager.persist(unitOfWork);
        return unitOfWork;
    }

    EntityState createEntityHistory(UnitOfWork unitOfWork, EntityOperation entityOperation) {
        EntityState entityState = new EntityState();
        entityState.setUnitOfWork(unitOfWork);
        entityState.setOperationType(entityOperation.getOperationType());
        entityState.setEntityName(entityOperation.getEntityIdentifier().getEntityName());
        entityState.setEntityId(entityOperation.getEntityIdentifier().getPrimaryKey().toString());
        entityManager.persist(entityState);

        if (entityOperation.getAttributes() != null) {
            entityOperation.getAttributes().entrySet().forEach(entry -> createEntityHistoryAttribute(entityState, entry));
        }

        return entityState;
    }

    EntityStateAttribute createEntityHistoryAttribute(EntityState entityState, Map.Entry<String, EntityAttributeData> field) {
        EntityStateAttribute entityStateAttribute = new EntityStateAttribute();
        entityStateAttribute.setEntityState(entityState);
        entityStateAttribute.setName(field.getKey());
        EntityAttributeData historyData = field.getValue();
        entityStateAttribute.setModified(historyData.isModified());
        historyData.getPreviousValue().getTextValue().ifPresent(entityStateAttribute::setPreviousValue);
        historyData.getValue().getTextValue().ifPresent(entityStateAttribute::setValue);

        entityManager.persist(entityStateAttribute);
        return entityStateAttribute;
    }

    public void saveFailure(UnitOfWorkLogger unitOfWorkLogger, UnitOfWork.Status status) {
        try {
            transactionTemplate.execute(txStatus -> {
                saveUnitOfWork(unitOfWorkLogger, status);
                return null;
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
