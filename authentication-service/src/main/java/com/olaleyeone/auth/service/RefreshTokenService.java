package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(AuthenticationResponse authenticationResponse);

    RefreshToken createRefreshToken(PortalUser portalUser);

    void deactivateRefreshToken(RefreshToken refreshToken);
}
