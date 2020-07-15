package com.olaleyeone.auth.messaging.producers;

import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Builder
@Component
public class UserPublisher {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final PortalUserRepository portalUserRepository;

    private final TransactionTemplate transactionTemplate;

    private final Provider<TaskContext> taskContextProvider;
    private final TaskContextFactory taskContextFactory;

    private final UserApiResponseHandler userApiResponseHandler;

    @Getter
    @Value("${user.topic.name}")
    private final String userTopic;

    public Future<?> publish(PortalUser portalUser) {
        CompletableFuture<?> completableFuture = new CompletableFuture<>();
        sendMessage(portalUser).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onFailure(Throwable ex) {
                logger.error(ex.getMessage(), ex);
                completableFuture.completeExceptionally(ex);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.info("User {} published", portalUser.getId());
                String description = String.format("Update published user %d", portalUser.getId());
                taskContextFactory.startBackgroundTask(
                        "UPDATE PUBLISHED USER",
                        description,
                        () -> taskContextProvider.get().execute(
                                "UPDATE PUBLISHED USER",
                                description,
                                () -> transactionTemplate.execute(status -> {
                                    portalUser.setPublishedOn(LocalDateTime.now());
                                    return portalUserRepository.save(portalUser);
                                })));
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    @SneakyThrows
    public ListenableFuture<SendResult<String, Object>> sendMessage(PortalUser msg) {
        return kafkaTemplate.send(userTopic, msg.getId().toString(), userApiResponseHandler.toUserApiResponse(msg));
    }
}
