package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;

@RequiredArgsConstructor
@Named
public class UserApiResponseHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public UserApiResponse getUserApiResponse(PortalUserAuthentication portalUserAuthentication) {
        PortalUser portalUser = portalUserAuthentication.getPortalUserIdentifier().getPortalUser();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        UserApiResponse userApiResponse = new UserApiResponse(portalUser);
        userApiResponse.setRefreshToken(jwtService.getRefreshToken(refreshToken));
        userApiResponse.setAccessToken(jwtService.getAccessToken(portalUser));
        return userApiResponse;
    }
}
