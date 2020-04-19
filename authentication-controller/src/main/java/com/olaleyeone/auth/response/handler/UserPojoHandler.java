package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.response.pojo.UserPojo;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;

@RequiredArgsConstructor
@Named
public class UserPojoHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public UserPojo getUserPojo(AuthenticationResponse authenticationResponse){
        PortalUser portalUser = authenticationResponse.getPortalUserIdentifier().getPortalUser();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticationResponse);

        UserPojo userPojo = new UserPojo(portalUser);
        userPojo.setRefreshToken(jwtService.getRefreshToken(refreshToken));
        userPojo.setAccessToken(jwtService.getAccessToken(portalUser));
        return userPojo;
    }
}
