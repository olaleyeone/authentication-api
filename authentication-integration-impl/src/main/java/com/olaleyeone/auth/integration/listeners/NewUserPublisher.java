package com.olaleyeone.auth.integration.listeners;

import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.integration.events.NewUserEvent;
import com.olaleyeone.auth.repository.PortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.inject.Provider;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class NewUserPublisher {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, PortalUser> kafkaTemplate;

    private final PortalUserRepository portalUserRepository;

    private final TransactionTemplate transactionTemplate;

    private final Provider<TaskContext> taskContextProvider;
    private final TaskContextFactory taskContextFactory;

    @Value("${new_user.topic.name}")
    private String newUserTopic;

    @EventListener(NewUserEvent.class)
    @Async
    public void newUserCreated(NewUserEvent newUserEvent) {
        logger.info("Event: User created");
        PortalUser portalUser = newUserEvent.getPortalUser();
        taskContextFactory.startBackgroundTask(
                "PUBLISH NEW USER",
                String.format("Publish new user %d", portalUser.getId()),
                () -> sendUser(portalUser));
    }

    private void sendUser(PortalUser portalUser) {
        sendMessage(portalUser).addCallback(new ListenableFutureCallback<SendResult<String, PortalUser>>() {

            @Override
            public void onFailure(Throwable ex) {
                //noop
                logger.error(ex.getMessage(), ex);
            }

            @Override
            public void onSuccess(SendResult<String, PortalUser> result) {
                logger.info("User published");
                taskContextProvider.get().execute(
                        "UPDATE PUBLISHED USER",
                        () -> transactionTemplate.execute(status -> {
                            portalUser.setPublishedOn(LocalDateTime.now());
                            portalUserRepository.save(portalUser);
                            return null;
                        }));
            }
        });
    }

    public ListenableFuture<SendResult<String, PortalUser>> sendMessage(PortalUser msg) {
        return kafkaTemplate.send(newUserTopic, msg);
    }
}
