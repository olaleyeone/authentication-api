package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PortalUserAuthenticationTest extends EntityTest {

    private PortalUserAuthentication portalUserAuthentication;

    @BeforeEach
    void setUp() {
        portalUserAuthentication = new PortalUserAuthentication();
        portalUserAuthentication.setType(AuthenticationType.LOGIN);
        portalUserAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        portalUserAuthentication.setIpAddress(faker.internet().ipV4Address());
        portalUserAuthentication.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    public void shouldAutoSetUserFromIdentifier() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        portalUserAuthentication.setPortalUserIdentifier(portalUserIdentifier);
        portalUserAuthentication.setIdentifier(portalUserIdentifier.getIdentifier());
        saveAndFlush(portalUserAuthentication);
        assertNotNull(portalUserIdentifier.getId());
        assertNotNull(portalUserIdentifier.getPortalUser());
        assertEquals(portalUserIdentifier.getPortalUser(), portalUserAuthentication.getPortalUser());
    }

    @Test
    public void shouldPreventConflictingUserData() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        portalUserAuthentication.setPortalUserIdentifier(portalUserIdentifier);
        portalUserAuthentication.setIdentifier(portalUserIdentifier.getIdentifier());
        portalUserAuthentication.setPortalUser(modelFactory.create(PortalUser.class));
        assertThrows(IllegalArgumentException.class, () -> saveAndFlush(portalUserAuthentication));
    }

    @Test
    public void lastUpdatedShouldChange() {
        PortalUserAuthentication userAuthentication = modelFactory.create(PortalUserAuthentication.class);
        userAuthentication.setLastUpdatedAt(null);
        saveAndFlush(userAuthentication);
        assertNotNull(userAuthentication.getLastUpdatedAt());
    }
}