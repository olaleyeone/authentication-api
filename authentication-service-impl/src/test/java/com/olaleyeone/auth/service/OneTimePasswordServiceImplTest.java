package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OneTimePasswordServiceImplTest extends ServiceTest {

    @Autowired
    private OneTimePasswordService oneTimePasswordService;

    @Autowired
    private HashService hashService;

    private String hash;

    private PortalUserIdentifier portalUserIdentifier;

    @BeforeEach
    public void setUp() {
        hash = faker.random().hex();
        Mockito.doReturn(hash).when(hashService).generateHash(Mockito.anyString());

        portalUserIdentifier = modelFactory.create(PortalUserIdentifier.class);
    }

    @Test
    void createVerificationForEmail() {
        Map.Entry<OneTimePassword, String> verification =
                oneTimePasswordService.createOTP(portalUserIdentifier);
        assertNotNull(verification.getKey());
        assertNotNull(verification.getKey().getId());
        assertEquals(hash, verification.getKey().getHash());
        Mockito.verify(hashService, Mockito.times(1)).generateHash(verification.getValue());
    }

    @Test
    void deactivatePreviousCode() {
        Map.Entry<OneTimePassword, String> verification1 =
                oneTimePasswordService.createOTP(portalUserIdentifier);
        Map.Entry<OneTimePassword, String> verification2 =
                oneTimePasswordService.createOTP(portalUserIdentifier);
        entityManager.flush();
        entityManager.refresh(verification1.getKey());
        assertNotNull(verification1.getKey().getDeactivatedAt());
        assertNull(verification2.getKey().getDeactivatedAt());
    }
}