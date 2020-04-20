package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.AccessTokenDto;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.JwtService;
import com.olaleyeone.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.inject.Named;

@RequiredArgsConstructor
@Named
public class AccessTokenApiResponseHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public HttpEntity<AccessTokenApiResponse> getAccessToken(PortalUserAuthentication portalUserAuthentication) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse(portalUserAuthentication.getPortalUser());
        String refreshTokenJws = jwtService.getRefreshToken(refreshToken);
        AccessTokenDto accessToken = jwtService.getAccessToken(refreshToken);

//        accessTokenApiResponse.setRefreshToken(refreshTokenJws);
//        accessTokenApiResponse.setAccessToken(accessToken.getToken());
        accessTokenApiResponse.setSecondsTillExpiry(accessToken.getSecondsTillExpiry());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.noStore());
        httpHeaders.setPragma("no-cache");

        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("refresh_token=%s; Max-Age=%d; Path=/; Secure; HttpOnly",
                refreshTokenJws, refreshToken.getSecondsTillExpiry()));
        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("access_token=%s; Max-Age=%d; Path=/; Secure; HttpOnly",
                accessToken.getToken(), accessToken.getSecondsTillExpiry()));
        return new HttpEntity(accessTokenApiResponse, httpHeaders);
    }
}
