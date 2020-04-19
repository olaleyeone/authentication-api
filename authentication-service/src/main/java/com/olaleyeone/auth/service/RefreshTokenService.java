package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication);

    void deactivateRefreshToken(RefreshToken refreshToken);
}
