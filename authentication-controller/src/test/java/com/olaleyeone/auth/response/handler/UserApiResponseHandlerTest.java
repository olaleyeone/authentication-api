package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserApiResponseHandlerTest extends ComponentTest {

    @Mock
    private PortalUserIdentifierRepository portalUserIdentifierRepository;
    @Mock
    private PortalUserDataRepository portalUserDataRepository;
    @Mock
    private PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @InjectMocks
    private UserApiResponseHandler userApiResponseHandler;

    @Test
    void toUserApiResponse() {
        PortalUser portalUser = modelFactory.make(PortalUser.class);

        Mockito.doReturn(Arrays.asList(dtoFactory.make(PortalUserData.class)))
                .when(portalUserDataRepository).findByPortalUser(Mockito.any());

        Mockito.doReturn(Arrays.asList(dtoFactory.make(PortalUserIdentifier.class)))
                .when(portalUserIdentifierRepository).findByPortalUser(Mockito.any());

        UserApiResponse userApiResponse = userApiResponseHandler.toUserApiResponse(portalUser);
        assertNotNull(userApiResponse);
        assertEquals(portalUser.getId(), userApiResponse.getId());
        assertEquals(portalUser.getFirstName(), userApiResponse.getFirstName());
        assertEquals(portalUser.getLastName(), userApiResponse.getLastName());

        assertEquals(1, userApiResponse.getIdentifiers().size());
        assertEquals(1, userApiResponse.getData().size());
    }
}