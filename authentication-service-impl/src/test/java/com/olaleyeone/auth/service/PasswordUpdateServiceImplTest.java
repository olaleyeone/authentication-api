package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.PasswordUpdateApiRequest;
import com.olaleyeone.auth.test.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUpdateServiceImplTest extends ServiceTest {

    @Autowired
    private PasswordUpdateService passwordUpdateService;

    @Autowired
    private PasswordService passwordService;

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
        List<RefreshToken> otherSessions = modelFactory.pipe(RefreshToken.class)
                .then(it -> {
                    it.getActualAuthentication().setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        entityManager.refresh(refreshToken);
        assertNull(refreshToken.getTimeDeactivated());
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNotNull(it.getTimeDeactivated());
        });
    }

    @Test
    void shouldNotDeactivateOtherSessions() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<RefreshToken> otherSessions = modelFactory.pipe(RefreshToken.class)
                .then(it -> {
                    it.getActualAuthentication().setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        apiRequest.setInvalidateOtherSessions(null);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        entityManager.refresh(refreshToken);
        assertNull(refreshToken.getTimeDeactivated());
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getTimeDeactivated());
        });
    }

    @Test
    void shouldNotDeactivateExpiredSessions() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<RefreshToken> otherSessions = modelFactory.pipe(RefreshToken.class)
                .then(it -> {
                    it.setExpiresAt(LocalDateTime.now());
                    it.getActualAuthentication().setPortalUser(refreshToken.getPortalUser());
                    return it;
                })
                .create(2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getTimeDeactivated());
        });
    }

    @Test
    void shouldNotDeactivatedSessionsOfOtherUsers() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        List<RefreshToken> otherSessions = modelFactory.create(RefreshToken.class, 2);
        passwordUpdateService.updatePassword(refreshToken, apiRequest);
        otherSessions.forEach(it -> {
            entityManager.refresh(it);
            assertNull(it.getTimeDeactivated());
        });
    }
}