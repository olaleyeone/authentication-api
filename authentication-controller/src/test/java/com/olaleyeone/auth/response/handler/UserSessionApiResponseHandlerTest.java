package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthenticationData;
import com.olaleyeone.auth.repository.PortalUserAuthenticationDataRepository;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.response.pojo.UserSessionApiResponse;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserSessionApiResponseHandlerTest extends ComponentTest {

    @Mock
    private PortalUserDataRepository portalUserDataRepository;
    @Mock
    private PortalUserAuthenticationDataRepository portalUserAuthenticationDataRepository;

    @InjectMocks
    private UserSessionApiResponseHandler userSessionApiResponseHandler;

    private PortalUser portalUser;
    private PortalUserAuthentication portalUserAuthentication;

    @BeforeEach
    void setUp() {
        portalUser = modelFactory.make(PortalUser.class);
        portalUser.setId(faker.number().randomNumber());

        portalUserAuthentication = new PortalUserAuthentication();
        portalUserAuthentication.setPortalUser(portalUser);
        portalUserAuthentication.setId(faker.number().randomNumber());
        portalUserAuthentication.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void toApiResponse() {
        Mockito.doReturn(Arrays.asList(getPortalUserData(), getPortalUserData(), getPortalUserData()))
                .when(portalUserDataRepository)
                .findByPortalUser(Mockito.any());
        Mockito.doReturn(Arrays.asList(getAuthenticationData(), getAuthenticationData()))
                .when(portalUserAuthenticationDataRepository)
                .findByPortalUserAuthentication(Mockito.any());

        UserSessionApiResponse userSessionApiResponse = userSessionApiResponseHandler.toApiResponse(portalUserAuthentication);
        assertNotNull(userSessionApiResponse);
        assertEquals(portalUser.getId().toString(), userSessionApiResponse.getUserId());
        assertEquals(portalUserAuthentication.getId().toString(), userSessionApiResponse.getSessionId());

        assertNotNull(userSessionApiResponse.getData());
        assertEquals(2, userSessionApiResponse.getData().size());

        assertNotNull(userSessionApiResponse.getUserData());
        assertEquals(3, userSessionApiResponse.getUserData().size());
    }

    PortalUserAuthenticationData getAuthenticationData() {
        PortalUserAuthenticationData data = new PortalUserAuthenticationData();
        data.setName(faker.lordOfTheRings().character());
        data.setValue(faker.lordOfTheRings().location());
        return data;
    }

    PortalUserData getPortalUserData() {
        PortalUserData data = new PortalUserData();
        data.setName(faker.lordOfTheRings().character());
        data.setValue(faker.lordOfTheRings().location());
        return data;
    }
}