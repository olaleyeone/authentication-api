package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.AuditData;

import java.util.Optional;

public class AuditDataImpl implements AuditData {

    private final Object value;

    public AuditDataImpl(Object value) {
        this.value = value;
    }

    @Override
    public Optional<Object> getData() {
        return Optional.ofNullable(value);
    }

    @Override
    public Optional<String> getTextValue() {
        return getData().map(Object::toString);
    }
}
