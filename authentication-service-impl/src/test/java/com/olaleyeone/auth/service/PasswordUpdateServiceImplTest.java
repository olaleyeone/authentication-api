package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.PasswordResetApiRequest;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
                    it.setAutoLogoutAt(OffsetDateTime.now());
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
    void applyPasswordResetWithAutoLogin() {
        PasswordResetApiRequest apiRequest = new PasswordResetApiRequest();
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(true);

        PasswordResetRequest passwordResetRequest = modelFactory.pipe(PasswordResetRequest.class)
                .then(it -> {
                    it.setAutoLogin(true);
                    return it;
                })
                .create();

        List<PortalUserAuthentication> otherSessions = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(null);
                    it.setPortalUser(passwordResetRequest.getPortalUser());
                    return it;
                })
                .create(2);

        Optional<PortalUserAuthentication> optionalPortalUserAuthentication = doPasswordReset(apiRequest, passwordResetRequest);

        assertTrue(optionalPortalUserAuthentication.isPresent());
        PortalUserAuthentication userAuthentication = optionalPortalUserAuthentication.get();
        assertEquals(requestMetadata.getIpAddress(), userAuthentication.getIpAddress());
        assertEquals(requestMetadata.getUserAgent(), userAuthentication.getUserAgent());

        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNotNull(it.getDeactivatedAt());
        });
    }

    @Test
    void applyPasswordResetWithoutAutoLogin() {
        PasswordResetApiRequest apiRequest = new PasswordResetApiRequest();
        apiRequest.setPassword(faker.internet().password());
        apiRequest.setInvalidateOtherSessions(true);

        PasswordResetRequest passwordResetRequest = modelFactory.pipe(PasswordResetRequest.class)
                .then(it -> {
                    it.setAutoLogin(false);
                    return it;
                })
                .create();

        Optional<PortalUserAuthentication> optionalPortalUserAuthentication = doPasswordReset(apiRequest, passwordResetRequest);

        assertFalse(optionalPortalUserAuthentication.isPresent());
    }

    private Optional<PortalUserAuthentication> doPasswordReset(PasswordResetApiRequest apiRequest, PasswordResetRequest passwordResetRequest) {
        Mockito.doReturn(faker.internet().ipV4Address()).when(requestMetadata).getIpAddress();
        Mockito.doReturn(faker.internet().userAgentAny()).when(requestMetadata).getUserAgent();
        Optional<PortalUserAuthentication> optionalPortalUserAuthentication = passwordUpdateService.updatePassword(passwordResetRequest, apiRequest);
        entityManager.flush();
        entityManager.refresh(passwordResetRequest);
        assertNull(passwordResetRequest.getDeactivatedOn());
        assertNotNull(passwordResetRequest.getUsedOn());
        return optionalPortalUserAuthentication;
    }
}