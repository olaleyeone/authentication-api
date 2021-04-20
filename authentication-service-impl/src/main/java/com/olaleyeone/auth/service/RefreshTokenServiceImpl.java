package com.olaleyeone.auth.service;

import com.olaleyeone.audittrail.api.Activity;
import com.olaleyeone.audittrail.context.TaskContext;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.data.dto.AccessTokenRequestDto;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.inject.Provider;
import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Named
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    public static final String REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES = "REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES";
    public static final String ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS = "ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS";
    private static final int REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE = 60 * 24;

    private final SettingService settingService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;
    private final Provider<TaskContext> taskContextProvider;

    @Activity("REFRESH TOKEN CREATION FOR USER")
    @Transactional
    @Override
    public RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication, AccessTokenRequestDto requestDto) {
        if (requestDto.getFirebaseToken() != null) {
            userAuthentication.setFirebaseToken(requestDto.getFirebaseToken().orElse(null));
        }
        return createRefreshToken(userAuthentication);
    }

    @Activity("REFRESH TOKEN CREATION FOR USER")
    @Transactional
    @Override
    public RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication) {
        taskContextProvider.get().setDescription(
                String.format("Create refresh token for logged in user %s", userAuthentication.getPortalUser().getId()));
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(userAuthentication);

        refreshToken.setExpiresAt(getExpiresAt(Optional.ofNullable(userAuthentication.getRefreshTokenDurationInSeconds())
                .orElseGet(() -> settingService.getInteger(REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES,
                        REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE) * 60)));
        refreshToken.setAccessExpiresAt(getAccessExpiresAt());
        refreshTokenRepository.save(refreshToken);

        userAuthentication.setLastActiveAt(refreshToken.getCreatedAt());
        userAuthentication.setBecomesInactiveAt(refreshToken.getAccessExpiresAt());
        userAuthentication.setAutoLogoutAt(refreshToken.getExpiresAt());
        userAuthentication.setPublishedAt(null);
        portalUserAuthenticationRepository.save(userAuthentication);
        return refreshToken;
    }

    private OffsetDateTime getExpiresAt(Integer durationInSeconds) {
        if (durationInSeconds <= 0) {
            return null;
        }
        return OffsetDateTime.now().plusSeconds(durationInSeconds);
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
        refreshToken.setDeactivatedAt(OffsetDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}
