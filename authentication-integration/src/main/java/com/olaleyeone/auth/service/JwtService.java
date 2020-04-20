package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.AccessTokenDto;

public interface JwtService {

    String getRefreshToken(RefreshToken refreshToken);

    AccessTokenDto getAccessToken(RefreshToken refreshToken);

    String getSubject(String token);
}
