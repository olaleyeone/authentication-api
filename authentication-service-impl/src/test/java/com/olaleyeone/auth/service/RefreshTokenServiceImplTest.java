package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.test.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenServiceImplTest extends ServiceTest {

    private AuthenticationResponse authenticationResponse;

    @Inject
    private RefreshTokenService refreshTokenService;

    @Inject
    private SettingService settingService;

    @BeforeEach
    public void setUp() {
        authenticationResponse = modelFactory.create(AuthenticationResponse.class);
    }

    @Test
    public void createRefreshTokenForAuthenticatedUser() {
        int duration = 5;
        settingService.getInteger(RefreshTokenServiceImpl.REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES, duration);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticationResponse);
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getId());
        assertEquals(authenticationResponse.getId(), refreshToken.getActualAuthentication().getId());
        int durationInSeconds = duration * 60;
        long actualExpiryDurationInSeconds = refreshToken.getDateCreated().until(refreshToken.getExpiresAt(), ChronoUnit.SECONDS);
        assertTrue((durationInSeconds - 1) == actualExpiryDurationInSeconds || durationInSeconds == actualExpiryDurationInSeconds);
    }

    @Test
    public void createRefreshTokenForPortal() {
        int duration = 5;
        PortalUser portalUser = modelFactory.create(PortalUser.class);
        settingService.getInteger(RefreshTokenServiceImpl.REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES, duration);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUser);
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getId());
        assertEquals(portalUser.getId(), refreshToken.getPortalUser().getId());
        int durationInSeconds = duration * 60;
        long actualExpiryDurationInSeconds = refreshToken.getDateCreated().until(refreshToken.getExpiresAt(), ChronoUnit.SECONDS);
        assertTrue((durationInSeconds - 1) == actualExpiryDurationInSeconds || durationInSeconds == actualExpiryDurationInSeconds);
    }

    @Test
    public void shouldPreventDuplicateRefreshToken() {
        int duration = 5;
        RefreshToken prevRefreshToken = modelFactory.pipe(RefreshToken.class)
                .then(it -> {
                    it.setActualAuthentication(authenticationResponse);
                    return it;
                }).create();
        settingService.getInteger(RefreshTokenServiceImpl.REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES, duration);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticationResponse);
        assertNotNull(refreshToken);
        assertNotNull(refreshToken.getId());
        assertEquals(authenticationResponse.getId(), refreshToken.getActualAuthentication().getId());
        assertNotNull(prevRefreshToken.getTimeDeactivated());
    }

    @Test
    public void deactivateRefreshToken() {
        RefreshToken refreshToken = modelFactory.create(RefreshToken.class);
        refreshTokenService.deactivateRefreshToken(refreshToken);
        assertNotNull(refreshToken.getTimeDeactivated());
    }
}