package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.AccessTokenDto;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import javax.inject.Named;

@RequiredArgsConstructor
@Named
public class AccessTokenApiResponseHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public ResponseEntity<AccessTokenApiResponse> getAccessToken(PortalUserAuthentication portalUserAuthentication) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse(portalUserAuthentication.getPortalUser());
        accessTokenApiResponse.setRefreshToken(jwtService.getRefreshToken(refreshToken));
        AccessTokenDto accessToken = jwtService.getAccessToken(refreshToken);
        accessTokenApiResponse.setAccessToken(accessToken.getToken());
        accessTokenApiResponse.setSecondsTillExpiry(accessToken.getSecondsTillExpiry());

//        httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
//        httpServletResponse.setHeader(HttpHeaders.PRAGMA, "no-cache");
        return ResponseEntity.ok(accessTokenApiResponse);
    }
}
