package com.olaleyeone.auth.event.listener;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.integration.events.NewUserEvent;
import com.olaleyeone.auth.messaging.producers.UserPublisher;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

class NewUserEventListenerTest extends ComponentTest {

    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private UserPublisher publisher;

    @InjectMocks
    private NewUserEventListener eventListener;

    @Test
    void handleEvent() {
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, Action.class).execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
        PortalUser portalUser = modelFactory.make(PortalUser.class);
        eventListener.handleEvent(new NewUserEvent(portalUser));
        Mockito.verify(publisher).publish(portalUser);
    }
}