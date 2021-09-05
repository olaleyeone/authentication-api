package com.olaleyeone.auth.messaging.producers;

import com.olaleyeone.auth.dto.PortalUserIdentifierVerificationRequestMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@Builder
@Component
public class PortalUserIdentifierVerificationRequestPublisher {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Getter
    @Value("${user_identifier_verification_code_request.topic.name}")
    private final String userTopic;

    @EventListener(PortalUserIdentifierVerificationRequestMessage.class)
    public Future<?> publish(PortalUserIdentifierVerificationRequestMessage msg) {
        CompletableFuture<?> completableFuture = new CompletableFuture<>();
        sendMessage(msg).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {

            @Override
            public void onFailure(Throwable ex) {
                logger.error(ex.getMessage(), ex);
                completableFuture.completeExceptionally(ex);
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                logger.info("User identifier verification request {} published", msg.getIdentifier());
                completableFuture.complete(null);
            }
        });
        return completableFuture;
    }

    public ListenableFuture<SendResult<String, Object>> sendMessage(PortalUserIdentifierVerificationRequestMessage msg) {
        return kafkaTemplate.send(userTopic, msg);
    }
}