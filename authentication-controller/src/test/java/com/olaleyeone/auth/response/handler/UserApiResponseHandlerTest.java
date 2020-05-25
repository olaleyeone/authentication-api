package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
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

    @InjectMocks
    private UserApiResponseHandler userApiResponseHandler;

    @Test
    void toUserApiResponse() {
        PortalUser portalUser = getPortalUser();

        Mockito.doReturn(Arrays.asList(getPortalUserData())).when(portalUserDataRepository).findByPortalUser(Mockito.any());
        Mockito.doReturn(Arrays.asList(getPortalUserIdentifier())).when(portalUserIdentifierRepository).findByPortalUser(Mockito.any());

        UserApiResponse userApiResponse = userApiResponseHandler.toUserApiResponse(portalUser);
        assertNotNull(userApiResponse);
        assertEquals(portalUser.getId(), userApiResponse.getId());
        assertEquals(portalUser.getFirstName(), userApiResponse.getFirstName());
        assertEquals(portalUser.getLastName(), userApiResponse.getLastName());

        assertEquals(1, userApiResponse.getIdentifiers().size());
        assertEquals(1, userApiResponse.getData().size());
    }

    PortalUser getPortalUser() {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());
        portalUser.setFirstName(faker.name().firstName());
        portalUser.setLastName(faker.name().lastName());
        return portalUser;
    }

    PortalUserData getPortalUserData() {
        PortalUserData portalUser = new PortalUserData();
        portalUser.setId(faker.number().randomNumber());
        portalUser.setName(faker.name().firstName());
        portalUser.setValue(faker.name().lastName());
        return portalUser;
    }

    PortalUserIdentifier getPortalUserIdentifier() {
        PortalUserIdentifier portalUser = new PortalUserIdentifier();
        portalUser.setId(faker.number().randomNumber());
        portalUser.setIdentifierType(UserIdentifierType.EMAIL);
        portalUser.setIdentifier(faker.name().username());
        return portalUser;
    }
}