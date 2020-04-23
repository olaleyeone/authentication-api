package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.EntityOperation;
import com.olaleyeone.audittrail.api.EntityAttributeData;
import com.olaleyeone.audittrail.api.EntityStateLogger;
import com.olaleyeone.audittrail.entity.EntityState;
import com.olaleyeone.audittrail.entity.EntityStateAttribute;
import com.olaleyeone.audittrail.entity.RequestLog;
import com.olaleyeone.audittrail.entity.UnitOfWork;
import lombok.Getter;
import org.springframework.transaction.support.TransactionSynchronization;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public abstract class UnitOfWorkLogger implements TransactionSynchronization {

    private final EntityStateLogger entityStateLogger;
    private final EntityManager entityManager;

    private final LocalDateTime startTime = LocalDateTime.now();

    public UnitOfWorkLogger(EntityStateLogger entityStateLogger, EntityManager entityManager) {
        this.entityStateLogger = entityStateLogger;
        this.entityManager = entityManager;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        List<EntityOperation> logs = entityStateLogger.getOperations();
        if (logs.isEmpty()) {
            return;
        }
        UnitOfWork unitOfWork = createUnitOfWork();
        logs.forEach(entityHistoryLog -> createEntityHistory(unitOfWork, entityHistoryLog));
    }

    public abstract Optional<RequestLog> getRequest();

    UnitOfWork createUnitOfWork() {
        UnitOfWork unitOfWork = new UnitOfWork();
        unitOfWork.setEstimatedTimeTakenInNanos(startTime.until(LocalDateTime.now(), ChronoUnit.NANOS));
        getRequest().ifPresent(unitOfWork::setRequest);
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
}
