package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.PortalUserAuthenticationRepository;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Named
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    public static String REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES = "REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES";
    public static String ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS = "ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS";
    private final int REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE = 30;

    private final SettingService settingService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PortalUserAuthenticationRepository portalUserAuthenticationRepository;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(userAuthentication);
        refreshToken.setExpiresAt(getExpiresAt());
        refreshToken.setAccessExpiresAt(getAccessExpiresAt());
        refreshTokenRepository.save(refreshToken);

        userAuthentication.setLastActiveAt(refreshToken.getDateCreated());
        userAuthentication.setBecomesInactiveAt(refreshToken.getAccessExpiresAt());
        userAuthentication.setAutoLogoutAt(refreshToken.getExpiresAt());
        return refreshToken;
    }

    private LocalDateTime getExpiresAt() {
        return LocalDateTime.now().plusMinutes(settingService.getInteger(REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES,
                REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE));
    }

    private LocalDateTime getAccessExpiresAt() {
        return LocalDateTime.now().plusSeconds(settingService.getInteger(ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS,
                180));
    }

    @Transactional
    @Override
    public void deactivateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setTimeDeactivated(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}
