package com.olaleyeone.audittrail.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@RequiredArgsConstructor
public class EntityIdentifier implements Serializable {

    private final Class<?> entityType;
    private final String entityName;
    private final Serializable primaryKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityIdentifier that = (EntityIdentifier) o;
        return entityType.getName().equals(that.entityType.getName()) &&
                primaryKey.equals(that.primaryKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType.getName(), primaryKey);
    }
}
