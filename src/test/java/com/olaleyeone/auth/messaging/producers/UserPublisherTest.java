package com.olaleyeone.auth.messaging.producers;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.response.handler.UserApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserPublisherTest extends ComponentTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private PortalUserRepository portalUserRepository;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private Provider<TaskContext> taskContextProvider;
    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private UserApiResponseHandler userApiResponseHandler;

    @Mock
    private TaskContext taskContext;

    public UserPublisher userPublisher;

    private PortalUser portalUser;

    @BeforeEach
    void setUp() {
        userPublisher = UserPublisher.builder()
                .kafkaTemplate(kafkaTemplate)
                .userTopic(faker.lordOfTheRings().character())
                .portalUserRepository(portalUserRepository)
                .taskContextFactory(taskContextFactory)
                .taskContextProvider(taskContextProvider)
                .transactionTemplate(transactionTemplate)
                .userApiResponseHandler(userApiResponseHandler)
                .build();
        portalUser = modelFactory.make(PortalUser.class);
        portalUser.setId(faker.number().randomNumber());
    }

    @Test
    void publish() {
        prepareMocks();

        userPublisher.publish(portalUser);
        assertNotNull(portalUser.getPublishedOn());
        Mockito.verify(portalUserRepository, Mockito.times(1))
                .save(portalUser);
    }

    private void prepareMocks() {
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, Action.class).execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.doReturn(taskContext).when(taskContextProvider).get();
        Mockito.doAnswer(invocation -> {
            invocation.getArgument(2, Action.class).execute();
            return null;
        }).when(taskContext).execute(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito.doAnswer(invocation -> invocation.getArgument(0, TransactionCallback.class).doInTransaction(null))
                .when(transactionTemplate).execute(Mockito.any());

        Mockito.doReturn(new AsyncResult<>(Mockito.mock(SendResult.class)))
                .when(kafkaTemplate)
                .send(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void publishWithError() {
        Mockito.doReturn(AsyncResult.forExecutionException(new RuntimeException()))
                .when(kafkaTemplate)
                .send(Mockito.any(), Mockito.any(), Mockito.any());

        userPublisher.publish(portalUser);
        assertNull(portalUser.getPublishedOn());
        Mockito.verify(portalUserRepository, Mockito.never())
                .save(portalUser);
    }

    @Test
    void sendMessage() {
        UserApiResponse userApiResponse = new UserApiResponse();
        Mockito.doReturn(userApiResponse).when(userApiResponseHandler).toUserApiResponse(Mockito.any());
        userPublisher.sendMessage(portalUser);
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(
                        userPublisher.getUserTopic(),
                        portalUser.getId().toString(),
                        userApiResponse);
        Mockito.verify(userApiResponseHandler, Mockito.times(1))
                .toUserApiResponse(portalUser);
    }
}