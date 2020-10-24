package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.servicetest.ServiceTest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestServiceImplTest extends ServiceTest {

    @Autowired
    private PasswordResetRequestService passwordResetRequestService;

    @Autowired
    private HashService hashService;

    @Autowired
    private RequestMetadata requestMetadata;

    private String hash;

    @BeforeEach
    public void setUp() {
        hash = faker.random().hex();
        Mockito.doReturn(hash).when(hashService).generateHash(Mockito.anyString());

        Mockito.doReturn(faker.internet().ipV4Address()).when(requestMetadata).getIpAddress();
        Mockito.doReturn(faker.internet().userAgentAny()).when(requestMetadata).getUserAgent();
    }

    @Test
    void createRequest() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier, true);
        assertNotNull(request.getKey());
        assertNotNull(request.getKey().getId());
        assertEquals(hash, request.getKey().getResetCodeHash());
        Mockito.verify(hashService, Mockito.times(1)).generateHash(request.getValue());
    }

    @Test
    void deactivatePreviousCode() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        Map.Entry<PasswordResetRequest, String> verification1 =
                passwordResetRequestService.createRequest(portalUserIdentifier, true);
        Map.Entry<PasswordResetRequest, String> verification2 =
                passwordResetRequestService.createRequest(portalUserIdentifier, false);
        entityManager.flush();
        entityManager.refresh(verification1.getKey());
        assertNotNull(verification1.getKey().getDeactivatedAt());
        assertNull(verification2.getKey().getDeactivatedAt());
    }
}