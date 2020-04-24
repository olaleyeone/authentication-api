package com.olaleyeone.audittrail.impl;

import com.olaleyeone.audittrail.api.ActivityLogger;
import com.olaleyeone.audittrail.entity.ActivityLog;
import lombok.Data;

import java.util.List;

@Data
public final class ActivityLoggerImpl implements ActivityLogger {

    private final List<ActivityLog> activityLogs;

    @Override
    public void log(String name) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log(name, null, stackTraceElement);
    }

    @Override
    public void log(String name, String description) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log(name, description, stackTraceElement);
    }

    private void log(String name, String description, StackTraceElement stackTraceElement) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setName(name);
        activityLog.setDescription(description);
        activityLog.setClassName(stackTraceElement.getClassName());
        activityLog.setMethodName(stackTraceElement.getMethodName());
        activityLog.setLineNumber(stackTraceElement.getLineNumber());
        activityLog.setPrecedence(activityLogs.size() + 1);
        activityLogs.add(activityLog);
    }
}
