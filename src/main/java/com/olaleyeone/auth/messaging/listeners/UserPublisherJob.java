package com.olaleyeone.auth.messaging.listeners;

import com.github.olaleyeone.entitysearch.JpaQuerySource;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.QPortalUser;
import com.olaleyeone.auth.messaging.producers.UserPublisher;
import com.olaleyeone.auth.repository.PortalUserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Builder
@Component
public class UserPublisherJob {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private final JpaQuerySource jpaQuerySource;
    private final UserPublisher messageProducer;
    private final TaskContextFactory taskContextFactory;
    private final PortalUserRepository portalUserRepository;

    private final AtomicReference<LocalDateTime> lastTrigger = new AtomicReference<>();
    private final Lock lock = new ReentrantLock();

    @SneakyThrows
    @KafkaListener(topics = "${task.publish_users.topic.name}", groupId = "${kafka.groupId}")
    public void listen(String message) {
        logger.info("{}", message);
        LocalDateTime startTime = LocalDateTime.now();
        lastTrigger.set(startTime);
        taskContextFactory.startBackgroundTask(
                "PUBLISH USERS",
                "Start background job to publish users",
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
        List<PortalUser> failures = new ArrayList<>();
        List<PortalUser> portalUsers;
        do {
            portalUsers = getNext(failures.size());
            portalUsers.forEach(portalUser -> {
                try {
                    messageProducer.publish(portalUser).get();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    failures.add(portalUser);
                }
            });
            if (startTime != lastTrigger.get()) {
                break;
            }
        } while (!portalUsers.isEmpty());
    }

    private List<PortalUser> getNext(int offset) {
        return jpaQuerySource.startQuery(QPortalUser.portalUser)
                .where(QPortalUser.portalUser.publishedOn.isNull())
                .offset(offset)
                .limit(20)
                .fetch();
    }
}
