package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PasswordUpdateServiceImplTest extends ServiceTest {

    @Autowired
    private PasswordUpdateService passwordUpdateService;

    @Autowired
    private HashService hashService;

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
}