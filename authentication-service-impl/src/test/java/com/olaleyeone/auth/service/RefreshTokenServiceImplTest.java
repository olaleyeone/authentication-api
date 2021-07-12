package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenServiceImplTest extends ServiceTest {

    private PortalUserAuthentication userAuthentication;

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private SettingService settingService;

    @BeforeEach
    public void setUp() {
        userAuthentication = modelFactory.create(PortalUserAuthentication.class);
    }

    @Test
    public void createRefreshTokenForAuthenticatedUser() {
        int duration = 5;
        settingService.getInteger(RefreshTokenServiceImpl.REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES, duration);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userAuthentication);
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getId());
        assertEquals(userAuthentication.getId(), refreshToken.getActualAuthentication().getId());
        int durationInSeconds = duration * 60;
        long actualExpiryDurationInSeconds = refreshToken.getCreatedAt().until(refreshToken.getExpiresAt(), ChronoUnit.SECONDS);
        assertTrue((durationInSeconds - 1) == actualExpiryDurationInSeconds || durationInSeconds == actualExpiryDurationInSeconds);
    }

    @Test
    public void deactivateRefreshToken() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        refreshTokenService.deactivateRefreshToken(refreshToken);
        assertNotNull(refreshToken.getDeactivatedAt());
    }
}