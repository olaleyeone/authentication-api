package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.servicetest.ServiceTest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUpdateServiceImplTest extends ServiceTest {

    @Autowired
    private PasswordUpdateService passwordUpdateService;

    @Autowired
    private RequestMetadata requestMetadata;

    private PasswordUpdateApiRequest apiRequest;

    @BeforeEach
    void setUp() {
        apiRequest = new PasswordUpdateApiRequest();
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(true);
    }

    @Test
    void shouldDeactivateOtherSessions() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<PortalUserAuthentication> otherSessions = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(null);
                    it.setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        entityManager.refresh(refreshToken);
        assertNull(refreshToken.getTimeDeactivated());
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNotNull(it.getDeactivatedAt());
        });
    }

    @Test
    void shouldNotDeactivateOtherSessions() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<PortalUserAuthentication> otherSessions = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(null);
                    it.setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        apiRequest.setInvalidateOtherSessions(null);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        entityManager.refresh(refreshToken);
        assertNull(refreshToken.getTimeDeactivated());
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getDeactivatedAt());
        });
    }

    @Test
    void shouldNotDeactivateExpiredSessions() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<PortalUserAuthentication> otherSessions = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(null);
                    it.setAutoLogoutAt(LocalDateTime.now());
                    it.setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getDeactivatedAt());
        });
    }

    @Test
    void shouldNotDeactivatedSessionsOfOtherUsers() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<PortalUserAuthentication> otherSessions = modelFactory.create(PortalUserAuthentication.class, 2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getDeactivatedAt());
        });
    }

    @Test
    void applyPasswordReset() {
        PasswordResetRequest passwordResetRequest = modelFactory.create(PasswordResetRequest.class);
        List<PortalUserAuthentication> otherSessions = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(null);
                    it.setPortalUser(passwordResetRequest.getPortalUser());
                    return it;
                })
                .create(2);

        Mockito.doReturn(faker.internet().ipV4Address()).when(requestMetadata).getIpAddress();
        Mockito.doReturn(faker.internet().userAgentAny()).when(requestMetadata).getUserAgent();
        PortalUserAuthentication userAuthentication = passwordUpdateService.updatePassword(passwordResetRequest, apiRequest);
        entityManager.flush();
        entityManager.refresh(passwordResetRequest);
        assertNull(passwordResetRequest.getDeactivatedOn());
        assertNotNull(passwordResetRequest.getUsedOn());
        assertNotNull(userAuthentication);
        assertEquals(requestMetadata.getIpAddress(), userAuthentication.getIpAddress());
        assertEquals(requestMetadata.getUserAgent(), userAuthentication.getUserAgent());

        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNotNull(it.getDeactivatedAt());
        });
    }
}