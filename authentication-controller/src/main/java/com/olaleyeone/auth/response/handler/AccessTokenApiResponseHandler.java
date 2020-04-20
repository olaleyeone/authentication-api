package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
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

    @JwtToken(JwtTokenType.ACCESS)
    private final JwtService accessTokenJwtService;
    @JwtToken(JwtTokenType.REFRESH)
    private final JwtService refreshTokenJwtService;

    public HttpEntity<AccessTokenApiResponse> getAccessToken(PortalUserAuthentication portalUserAuthentication) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse(portalUserAuthentication.getPortalUser());
        JwtDto refreshTokenJwt = refreshTokenJwtService.generateJwt(refreshToken);
        JwtDto accessTokenJwt = accessTokenJwtService.generateJwt(refreshToken);

//        accessTokenApiResponse.setRefreshToken(refreshTokenJwt);
//        accessTokenApiResponse.setAccessToken(accessTokenJwt.getToken());
        accessTokenApiResponse.setSecondsTillExpiry(accessTokenJwt.getSecondsTillExpiry());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.noStore());
        httpHeaders.setPragma("no-cache");

        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("refresh_token=%s; Max-Age=%d; Path=/; Secure; HttpOnly",
                refreshTokenJwt.getToken(), refreshToken.getSecondsTillExpiry()));
        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("access_token=%s; Max-Age=%d; Path=/; Secure; HttpOnly",
                accessTokenJwt.getToken(), accessTokenJwt.getSecondsTillExpiry()));
        return new HttpEntity(accessTokenApiResponse, httpHeaders);
    }
}
