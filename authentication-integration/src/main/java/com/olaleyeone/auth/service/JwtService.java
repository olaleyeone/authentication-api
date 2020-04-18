package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;

public interface JwtService {

    String getRefreshToken(RefreshToken refreshToken);

    String getAccessToken(PortalUser portalUser);

    String getSubject(String token);
}
