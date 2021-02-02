package com.olaleyeone.auth.messaging.listeners;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.service.LogoutService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Builder
@Component
public class ReplacedUserSessionListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final TaskContextFactory taskContextFactory;

    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    private final LogoutService logoutService;
    private final ApplicationContext applicationContext;

    @SneakyThrows
    @KafkaListener(topics = "${replaced_user_session.topic.name}", groupId = "${kafka.groupId}")
    public void listen(String message) {
        logger.info("{}", message);
        taskContextFactory.startBackgroundTask(
                "DEACTIVATE REPLACED USER SESSION",
                "Start background job to deactivate replaced user session",
                () -> startTask(message));
    }

    @SneakyThrows
    private void startTask(String message) {
        Long id = Long.valueOf(message.replaceAll("\"", ""));
        portalUserAuthenticationRepository.findById(id)
                .ifPresent(portalUserAuthentication -> {
                    logoutService.deactivate(portalUserAuthentication);
                    applicationContext.publishEvent(new SessionUpdateEvent(portalUserAuthentication));
                });
    }
}
