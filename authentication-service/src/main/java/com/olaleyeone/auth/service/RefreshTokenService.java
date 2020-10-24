package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.data.dto.AccessTokenRequestDto;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication, AccessTokenRequestDto requestDto);

    RefreshToken createRefreshToken(PortalUserAuthentication userAuthentication);

    void deactivateRefreshToken(RefreshToken refreshToken);
}
