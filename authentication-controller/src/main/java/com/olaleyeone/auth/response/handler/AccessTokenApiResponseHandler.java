package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.integration.auth.JwtService;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Named
public class AccessTokenApiResponseHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String TOKEN_ENDPOINT = "/oauth2/token";

    private final HttpServletRequest httpServletRequest;
    private final RefreshTokenService refreshTokenService;

    @JwtToken(JwtTokenType.ACCESS)
    private final JwtService accessTokenJwtService;
    @JwtToken(JwtTokenType.REFRESH)
    private final JwtService refreshTokenJwtService;

    public HttpEntity<AccessTokenApiResponse> getAccessToken(PortalUserAuthentication portalUserAuthentication) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        return getUserApiResponseHttpEntity(refreshToken);
    }

    public HttpEntity<AccessTokenApiResponse> getAccessToken(RefreshToken currentRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(currentRefreshToken.getActualAuthentication());

        return getUserApiResponseHttpEntity(refreshToken);
    }

    private HttpEntity<AccessTokenApiResponse> getUserApiResponseHttpEntity(RefreshToken refreshToken) {
        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse(refreshToken.getPortalUser());
        JwtDto refreshTokenJwt = refreshTokenJwtService.generateJwt(refreshToken);
        JwtDto accessTokenJwt = accessTokenJwtService.generateJwt(refreshToken);

//        accessTokenApiResponse.setRefreshToken(refreshTokenJwt);
//        accessTokenApiResponse.setAccessToken(accessTokenJwt.getToken());

        accessTokenApiResponse.setExpiresAt(refreshToken.getAccessExpiresAt());
        accessTokenApiResponse.setSecondsTillExpiry(accessTokenJwt.getSecondsTillExpiry());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.noStore());
        httpHeaders.setPragma("no-cache");

        if (httpServletRequest.isSecure()) {
            httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=%s; Secure; HttpOnly",
                    REFRESH_TOKEN_COOKIE_NAME,
                    refreshTokenJwt.getToken(),
                    refreshToken.getSecondsTillExpiry(),
                    "/"));//httpServletRequest.getContextPath() + TOKEN_ENDPOINT

            httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=/; Secure; HttpOnly",
                    ACCESS_TOKEN_COOKIE_NAME,
                    accessTokenJwt.getToken(),
                    accessTokenJwt.getSecondsTillExpiry()
            ));
        } else {
            httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly",
                    REFRESH_TOKEN_COOKIE_NAME,
                    refreshTokenJwt.getToken(),
                    refreshToken.getSecondsTillExpiry()));

            httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=/; HttpOnly",
                    ACCESS_TOKEN_COOKIE_NAME,
                    accessTokenJwt.getToken(),
                    accessTokenJwt.getSecondsTillExpiry()
            ));
        }
        return new HttpEntity(accessTokenApiResponse, httpHeaders);
    }
}
