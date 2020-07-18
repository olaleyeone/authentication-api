package com.olaleyeone.auth.event.listener;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.messaging.producers.UserSessionPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SessionUpdateEventListener {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TaskContextFactory taskContextFactory;
    private final UserSessionPublisher publisher;

    @EventListener(SessionUpdateEvent.class)
    @Async
    public void handleEvent(SessionUpdateEvent event) {
        PortalUserAuthentication userAuthentication = event.getUserAuthentication();
        taskContextFactory.startBackgroundTask(
                "PUBLISH SESSION UPDATE",
                String.format("Publish session %d", userAuthentication.getId()),
                () -> publisher.publish(userAuthentication));
    }
}
