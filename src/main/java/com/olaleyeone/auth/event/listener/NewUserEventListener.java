package com.olaleyeone.auth.event.listener;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.integration.events.NewUserEvent;
import com.olaleyeone.auth.messaging.producers.UserPublisher;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NewUserEventListener {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TaskContextFactory taskContextFactory;
    private final UserPublisher publisher;

    @EventListener(NewUserEvent.class)
    @Async
    public void talentEvent(NewUserEvent event) {
        PortalUser portalUser = event.getPortalUser();
        taskContextFactory.startBackgroundTask(
                "PUBLISH NEW USER",
                String.format("Publish new user %d", portalUser.getId()),
                () -> publisher.publish(portalUser));
    }
}
