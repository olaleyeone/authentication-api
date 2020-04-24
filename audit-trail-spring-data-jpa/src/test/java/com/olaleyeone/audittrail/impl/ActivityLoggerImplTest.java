package com.olaleyeone.audittrail.impl;

import com.ComponentTest;
import com.olaleyeone.audittrail.entity.ActivityLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLoggerImplTest extends ComponentTest {

    private ActivityLoggerImpl activityLogger;

    @BeforeEach
    public void setUp(){
        activityLogger = new ActivityLoggerImpl(new ArrayList<>());
    }

    @Test
    void log() {
        activityLogger.log(faker.funnyName().name(), faker.backToTheFuture().quote());
        ActivityLog activityLog = activityLogger.getActivityLogs().iterator().next();
        assertEquals(getClass().getName(), activityLog.getClassName());
    }

    @Test
    void testLog() {
        activityLogger.log(faker.funnyName().name(), faker.backToTheFuture().quote());
        ActivityLog activityLog = activityLogger.getActivityLogs().iterator().next();
        assertEquals(getClass().getName(), activityLog.getClassName());
    }
}