package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(AuthenticationResponse authenticationResponse);

    void deactivateRefreshToken(RefreshToken refreshToken);
}
