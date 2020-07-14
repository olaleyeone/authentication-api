package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication);

    void deactivateRefreshToken(RefreshToken refreshToken);
}
