package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.*;

import java.util.HashMap;
import java.util.Map;

public class EntityUpdateOperation extends EntityOperation {

    public EntityUpdateOperation(EntityIdentifier entityIdentifier, Map<String, AuditData> from, Map<String, AuditData> to) {
        super(entityIdentifier, OperationType.UPDATE);
        Map<String, EntityAttributeData> dataUpdateMap = new HashMap<>();

        to.entrySet().forEach(entry -> {
            EntityAttributeData entityAttributeData = new EntityAttributeData(entry.getValue());
            entityAttributeData.setPreviousValue(from.get(entry.getKey()));
            dataUpdateMap.put(entry.getKey(), entityAttributeData);
        });

        setAttributes(dataUpdateMap);
    }
}
