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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.inject.Named;

@RequiredArgsConstructor
@Named
public class AccessTokenApiResponseHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String TOKEN_ENDPOINT = "/oauth2/token";

    private final RefreshTokenService refreshTokenService;

    @JwtToken(JwtTokenType.ACCESS)
    private final JwtService accessTokenJwtService;
    @JwtToken(JwtTokenType.REFRESH)
    private final JwtService refreshTokenJwtService;

    @Value("${context.path}")
    private final String contextPath;

    @Value("${cookie.flags}")
    private final String cookieFlags;

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

        accessTokenApiResponse.setRefreshToken(refreshTokenJwt.getToken());
        accessTokenApiResponse.setAccessToken(accessTokenJwt.getToken());

        accessTokenApiResponse.setExpiresAt(refreshToken.getAccessExpiresAt());
        accessTokenApiResponse.setSecondsTillExpiry(accessTokenJwt.getSecondsTillExpiry());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setCacheControl(CacheControl.noStore());
        httpHeaders.setPragma("no-cache");

        String refreshTokenPath = StringUtils.isBlank(contextPath) ? "/" : (contextPath + TOKEN_ENDPOINT);
        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=%s;%s",
                REFRESH_TOKEN_COOKIE_NAME,
                refreshTokenJwt.getToken(),
                refreshToken.getSecondsTillExpiry(),
                refreshTokenPath,
                StringUtils.defaultString(cookieFlags)));

        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format("%s=%s; Max-Age=%d; Path=/;%s",
                ACCESS_TOKEN_COOKIE_NAME,
                accessTokenJwt.getToken(),
                accessTokenJwt.getSecondsTillExpiry(),
                StringUtils.defaultString(cookieFlags)
        ));
        return new HttpEntity(accessTokenApiResponse, httpHeaders);
    }
}
