package com.olaleyeone.auth.messaging.producers;

import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.response.handler.UserSessionApiResponseHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.inject.Provider;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Builder
@Component
public class UserSessionPublisher {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    private final TransactionTemplate transactionTemplate;

    private final Provider<TaskContext> taskContextProvider;
    private final TaskContextFactory taskContextFactory;

    private final UserSessionApiResponseHandler responseHandler;

    @Getter
    @Value("${user_session.topic.name}")
    private final String userTopic;

    public Future<?> publish(PortalUserAuthentication userAuthentication) {
        CompletableFuture<?> completableFuture = new CompletableFuture<>();
        sendMessage(userAuthentication).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onFailure(Throwable ex) {
                logger.error(ex.getMessage(), ex);
                completableFuture.completeExceptionally(ex);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.info("Session {} published", userAuthentication.getId());
                String description = String.format("Update published session %d", userAuthentication.getId());
                taskContextFactory.startBackgroundTask(
                        "UPDATE PUBLISHED SESSION",
                        description,
                        () -> taskContextProvider.get().execute(
                                "UPDATE PUBLISHED SESSION",
                                description,
                                () -> transactionTemplate.execute(status -> {
                                    userAuthentication.setPublishedOn(OffsetDateTime.now());
                                    return portalUserAuthenticationRepository.save(userAuthentication);
                                })));
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    public ListenableFuture<SendResult<String, Object>> sendMessage(PortalUserAuthentication msg) {
        return kafkaTemplate.send(userTopic, msg.getId().toString(), responseHandler.toApiResponse(msg));
    }
}
