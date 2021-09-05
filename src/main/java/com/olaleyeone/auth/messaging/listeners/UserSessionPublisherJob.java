package com.olaleyeone.auth.messaging.listeners;

import com.github.olaleyeone.entitysearch.JpaQuerySource;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.QPortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.messaging.producers.UserSessionPublisher;
import com.olaleyeone.auth.repository.PortalUserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Builder
@Component
public class UserSessionPublisherJob {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final JpaQuerySource jpaQuerySource;
    private final UserSessionPublisher messageProducer;
    private final TaskContextFactory taskContextFactory;
    private final PortalUserRepository portalUserRepository;

    private final AtomicReference<LocalDateTime> lastTrigger = new AtomicReference<>();
    private final Lock lock = new ReentrantLock();

    @SneakyThrows
    @KafkaListener(topics = "${task.publish_user_sessions.topic.name}", groupId = "${kafka.groupId}")
    public void listen(String message) {
        logger.info("{}", message);
        LocalDateTime startTime = LocalDateTime.now();
        lastTrigger.set(startTime);
        taskContextFactory.startBackgroundTask(
                "PUBLISH USER SESSIONS",
                "Start background job to publish user sessions",
                () -> {
                    try {
                        lock.lock();
                        startTask(startTime);
                    } finally {
                        lock.unlock();
                    }
                });
    }

    private void startTask(LocalDateTime startTime) {
        List<PortalUserAuthentication> failures = new ArrayList<>();
        List<PortalUserAuthentication> userAuthentications;
        do {
            userAuthentications = getNext(failures.size());
            userAuthentications.forEach(userAuthentication -> {
                try {
                    messageProducer.publish(userAuthentication).get();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    failures.add(userAuthentication);
                }
            });
            if (startTime != lastTrigger.get()) {
                break;
            }
        } while (!userAuthentications.isEmpty());
    }

    private List<PortalUserAuthentication> getNext(int offset) {
        return jpaQuerySource.startQuery(QPortalUserAuthentication.portalUserAuthentication)
                .innerJoin(QPortalUserAuthentication.portalUserAuthentication.portalUser)
                .where(QPortalUserAuthentication.portalUserAuthentication.responseType.eq(AuthenticationResponseType.SUCCESSFUL))
                .where(QPortalUserAuthentication.portalUserAuthentication.publishedAt.isNull())
                .offset(offset)
                .limit(20)
                .fetch();
    }
}
