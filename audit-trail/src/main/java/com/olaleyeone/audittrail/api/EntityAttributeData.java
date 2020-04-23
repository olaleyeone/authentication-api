package com.olaleyeone.audittrail.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class EntityAttributeData {

    private AuditData previousValue;
    private final AuditData value;

    public boolean isModified() {
        if (previousValue == null && value == null) {
            return false;
        }
        return previousValue == null || value == null || !previousValue.getData().equals(value.getData());
    }

    @Override
    public String toString() {
        return String.format("%s->%s", getPreviousValue().getData(), getValue().getData());
    }
}
