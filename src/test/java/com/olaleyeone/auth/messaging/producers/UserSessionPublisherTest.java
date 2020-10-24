package com.olaleyeone.auth.messaging.producers;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.response.handler.UserSessionApiResponseHandler;
import com.olaleyeone.auth.response.pojo.UserSessionApiResponse;
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

class UserSessionPublisherTest extends ComponentTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private Provider<TaskContext> taskContextProvider;
    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private UserSessionApiResponseHandler userSessionApiResponseHandler;

    @Mock
    private TaskContext taskContext;

    public UserSessionPublisher publisher;

    private PortalUserAuthentication portalUserAuthentication;

    @BeforeEach
    void setUp() {
        publisher = UserSessionPublisher.builder()
                .kafkaTemplate(kafkaTemplate)
                .userTopic(faker.lordOfTheRings().character())
                .portalUserAuthenticationRepository(portalUserAuthenticationRepository)
                .taskContextFactory(taskContextFactory)
                .taskContextProvider(taskContextProvider)
                .transactionTemplate(transactionTemplate)
                .responseHandler(userSessionApiResponseHandler)
                .build();
        portalUserAuthentication = dtoFactory.make(PortalUserAuthentication.class);
        portalUserAuthentication.setId(faker.number().randomNumber());
    }

    @Test
    void publish() {
        prepareMocks();

        publisher.publish(portalUserAuthentication);
        assertNotNull(portalUserAuthentication.getPublishedAt());
        Mockito.verify(portalUserAuthenticationRepository, Mockito.times(1))
                .save(portalUserAuthentication);
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

        publisher.publish(portalUserAuthentication);
        assertNull(portalUserAuthentication.getPublishedAt());
        Mockito.verify(portalUserAuthenticationRepository, Mockito.never())
                .save(portalUserAuthentication);
    }

    @Test
    void sendMessage() {
        UserSessionApiResponse apiResponse = new UserSessionApiResponse();
        Mockito.doReturn(apiResponse).when(userSessionApiResponseHandler).toApiResponse(Mockito.any());
        publisher.sendMessage(portalUserAuthentication);
        Mockito.verify(kafkaTemplate, Mockito.times(1))
                .send(
                        publisher.getUserTopic(),
                        portalUserAuthentication.getId().toString(),
                        apiResponse);
        Mockito.verify(userSessionApiResponseHandler, Mockito.times(1))
                .toApiResponse(portalUserAuthentication);
    }
}