package com.olaleyeone.auth.event.listener;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.messaging.producers.UserSessionPublisher;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class SessionUpdateEventListenerTest extends ComponentTest {

    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private UserSessionPublisher publisher;

    @InjectMocks
    private SessionUpdateEventListener eventListener;

    @Test
    void handleEvent() {
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, Action.class).execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
        PortalUserAuthentication userAuthentication = dtoFactory.make(PortalUserAuthentication.class);
        eventListener.handleEvent(new SessionUpdateEvent(userAuthentication));
        Mockito.verify(publisher).publish(userAuthentication);
    }
}