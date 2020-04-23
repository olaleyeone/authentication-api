package com.olaleyeone.audittrail.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Builder
@Data
public class EntityAttributeData {

    @NonNull
    private final AuditData previousValue;
    @NonNull
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
