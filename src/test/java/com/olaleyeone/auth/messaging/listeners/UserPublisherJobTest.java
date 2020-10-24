package com.olaleyeone.auth.messaging.listeners;

import com.github.olaleyeone.entitysearch.JpaQuerySource;
import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.test.entity.EntityTest;
import com.olaleyeone.auth.messaging.producers.UserPublisher;
import com.olaleyeone.auth.repository.PortalUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserPublisherJobTest extends EntityTest {

    @Autowired
    private JpaQuerySource jpaQuerySource;
    @Autowired
    private PortalUserRepository portalUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Mock
    private UserPublisher messageProducer;
    @Mock
    private TaskContextFactory taskContextFactory;

    private UserPublisherJob userPublisherJob;

    @BeforeEach
    public void setUp() {
        userPublisherJob = UserPublisherJob.builder()
                .portalUserRepository(portalUserRepository)
                .jpaQuerySource(jpaQuerySource)
                .messageProducer(messageProducer)
                .taskContextFactory(taskContextFactory)
                .build();

        Mockito.doAnswer(invocation -> {
            ((Action) invocation.getArgument(2)).execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void listenWithData() {
        PortalUser portalUser = modelFactory.pipe(PortalUser.class)
                .then(it -> {
                    it.setPublishedAt(null);
                    return it;
                })
                .create();

        Mockito.doAnswer(invocation -> {
            portalUser.setPublishedAt(OffsetDateTime.now());
            entityManager.merge(portalUser);
            assertNotNull(portalUser.getPublishedAt());
            return CompletableFuture.completedFuture(null);
        }).when(messageProducer).publish(Mockito.any());

        userPublisherJob.listen("");
        assertNotNull(portalUser.getPublishedAt());
    }

    @Test
    void listenWithoutData() {
        modelFactory.pipe(PortalUser.class)
                .then(it -> {
                    it.setPublishedAt(OffsetDateTime.now());
                    return it;
                })
                .create();

        userPublisherJob.listen("");
        Mockito.verify(messageProducer, Mockito.never()).publish(Mockito.any());
    }

    @Test
    void listenWithException() {
        PortalUser portalUser = modelFactory.pipe(PortalUser.class)
                .then(it -> {
                    it.setPublishedAt(null);
                    return it;
                })
                .create();

        Mockito.doAnswer(invocation -> {
            throw new RuntimeException();
        }).when(messageProducer).publish(Mockito.any());
        userPublisherJob.listen("");
        Mockito.verify(messageProducer, Mockito.times(1)).publish(portalUser);
    }
}