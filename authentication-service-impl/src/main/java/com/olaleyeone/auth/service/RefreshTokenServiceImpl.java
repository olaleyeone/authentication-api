package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Named
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    public static String REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES = "REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES";
    private final int REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE = 30;

    private final SettingService settingService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(AuthenticationResponse authenticationResponse) {
        refreshTokenRepository.findActiveTokens(authenticationResponse, LocalDateTime.now())
                .forEach(this::deactivateRefreshToken);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(authenticationResponse);
        refreshToken.setPortalUser(authenticationResponse.getPortalUserIdentifier().getPortalUser());
        refreshToken.setExpiresAt(getExpiresAt());
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    @Override
    public RefreshToken createRefreshToken(PortalUser portalUser) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setPortalUser(portalUser);
        refreshToken.setExpiresAt(getExpiresAt());
        return refreshTokenRepository.save(refreshToken);
    }

    private LocalDateTime getExpiresAt() {
        return LocalDateTime.now().plusMinutes(settingService.getInteger(REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES,
                REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES_VALUE));
    }

    @Transactional
    @Override
    public void deactivateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setTimeDeactivated(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}
