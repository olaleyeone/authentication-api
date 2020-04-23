package com.olaleyeone.audittrail.api;

import lombok.Data;

import java.util.Map;

@Data
public class EntityOperation {

    private final EntityIdentifier entityIdentifier;
    private final OperationType operationType;

    private Map<String, EntityAttributeData> attributes;

    public EntityOperation(EntityIdentifier entityIdentifier, OperationType operationType) {
        this.entityIdentifier = entityIdentifier;
        this.operationType = operationType;
    }

    @Override
    public String toString() {
        if (attributes != null) {
            return String.format("%s %s (%s)", operationType, entityIdentifier, attributes);
        }
        return String.format("%s %s", operationType, entityIdentifier);
    }
}
