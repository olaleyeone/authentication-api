package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.servicetest.ServiceTest;
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

    private String hash;

    @BeforeEach
    public void setUp() {
        hash = faker.random().hex();
        Mockito.doReturn(hash).when(hashService).generateHash(Mockito.anyString());
    }

    @Test
    void createRequest() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier);
        assertNotNull(request.getKey());
        assertNotNull(request.getKey().getId());
        assertEquals(hash, request.getKey().getResetCodeHash());
        Mockito.verify(hashService, Mockito.times(1)).generateHash(request.getValue());
    }

    @Test
    void deactivatePreviousCode() {
        PortalUserIdentifier portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
        Map.Entry<PasswordResetRequest, String> verification1 =
                passwordResetRequestService.createRequest(portalUserIdentifier);
        Map.Entry<PasswordResetRequest, String> verification2 =
                passwordResetRequestService.createRequest(portalUserIdentifier);
        entityManager.flush();
        entityManager.refresh(verification1.getKey());
        assertNotNull(verification1.getKey().getDeactivatedOn());
        assertNull(verification2.getKey().getDeactivatedOn());
    }
}