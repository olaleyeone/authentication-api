package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
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

    private final SettingService settingService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public RefreshToken createRefreshToken(AuthenticationResponse authenticationResponse) {
        refreshTokenRepository.findActiveTokens(authenticationResponse, LocalDateTime.now())
                .forEach(this::deactivateRefreshToken);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(authenticationResponse);
        refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(settingService.getInteger(REFRESH_TOKEN_EXPIRY_DURATION_IN_MINUTES, 30)));
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    @Override
    public void deactivateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setTimeDeactivated(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
}
