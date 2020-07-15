package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;

@Named
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    public static final String REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES = "REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES";
    public static final String ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS = "ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS";
    private static final int REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE = 60 * 24;

    private final SettingService settingService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Provider<TaskContext> taskContextProvider;

    @Activity("REFRESH TOKEN CREATION FOR USER")
    @Transactional
    @Override
    public RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication) {
        taskContextProvider.get().setDescription(
                String.format("Create refresh token for logged in user %s", userAuthentication.getPortalUser().getId()));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(userAuthentication);
        refreshToken.setExpiresAt(getExpiresAt());
        refreshToken.setAccessExpiresAt(getAccessExpiresAt());
        refreshTokenRepository.save(refreshToken);

        userAuthentication.setLastActiveAt(refreshToken.getCreatedOn());
        userAuthentication.setBecomesInactiveAt(refreshToken.getAccessExpiresAt());
        userAuthentication.setAutoLogoutAt(refreshToken.getExpiresAt());
        userAuthentication.setPublishedOn(null);
        return refreshToken;
    }

    private OffsetDateTime getExpiresAt() {
        Integer durationInMinutes = settingService.getInteger(REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES,
                REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE);
        if (durationInMinutes <= 0) {
            return null;
        }
        return OffsetDateTime.now().plusMinutes(durationInMinutes);
    }

    private OffsetDateTime getAccessExpiresAt() {
        return OffsetDateTime.now().plusSeconds(settingService.getInteger(ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS,
                180));
    }

    @Activity("TOKEN DEACTIVATION")
    @Transactional
    @Override
    public void deactivateRefreshToken(RefreshToken refreshToken) {
        taskContextProvider.get().setDescription(String.format("deactivate token with id %d", refreshToken.getId()));
        refreshToken.setTimeDeactivated(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}
