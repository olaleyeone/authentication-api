package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.integration.security.AuthTokenGenerator;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.response.pojo.UserDataApiResponse;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.data.dto.AccessTokenRequestDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

import static com.olaleyeone.auth.response.handler.UserApiResponseHandler.getIdentifiers;

@RequiredArgsConstructor
@Builder
@Named
public class AccessTokenApiResponseHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String TOKEN_ENDPOINT = "/oauth2/token";

    private final RefreshTokenService refreshTokenService;

    @JwtToken(JwtTokenType.ACCESS)
    private final AuthTokenGenerator accessTokenJwtService;

    @JwtToken(JwtTokenType.REFRESH)
    private final AuthTokenGenerator refreshTokenJwtService;

    @Value("${deployment.path}")
    private final String contextPath;

    @Value("${cookie.flags}")
    private final String cookieFlags;

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PortalUserDataRepository portalUserDataRepository;
    private final ApplicationContext applicationContext;

    public HttpEntity<AccessTokenApiResponse> getAccessToken(PortalUserAuthentication portalUserAuthentication) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(portalUserAuthentication);

        return getUserApiResponseHttpEntity(refreshToken);
    }

    public HttpEntity<AccessTokenApiResponse> getAccessToken(RefreshToken currentRefreshToken, AccessTokenRequestDto requestDto) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(currentRefreshToken.getActualAuthentication(), requestDto);
        applicationContext.publishEvent(new SessionUpdateEvent(refreshToken.getActualAuthentication()));
        return getUserApiResponseHttpEntity(refreshToken);
    }

    public HttpEntity<AccessTokenApiResponse> getAccessToken(RefreshToken currentRefreshToken) {
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(currentRefreshToken.getActualAuthentication());
        applicationContext.publishEvent(new SessionUpdateEvent(refreshToken.getActualAuthentication()));
        return getUserApiResponseHttpEntity(refreshToken);
    }

    private HttpEntity<AccessTokenApiResponse> getUserApiResponseHttpEntity(RefreshToken refreshToken) {
        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse(refreshToken.getPortalUser());
        List<PortalUserIdentifier> userIdentifiers = portalUserIdentifierRepository.findByPortalUser(refreshToken.getPortalUser());

        accessTokenApiResponse.setEmailAddresses(getIdentifiers(userIdentifiers, UserIdentifierType.EMAIL_ADDRESS));
        accessTokenApiResponse.setPhoneNumbers(getIdentifiers(userIdentifiers, UserIdentifierType.PHONE_NUMBER));

        accessTokenApiResponse.setData(portalUserDataRepository.findByPortalUser(refreshToken.getPortalUser())
                .stream()
                .map(portalUserData -> new UserDataApiResponse(portalUserData.getName(), portalUserData.getValue()))
                .collect(Collectors.toList()));

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
        httpHeaders.add(HttpHeaders.SET_COOKIE, String.format

                ("%s=%s; Max-Age=%d; Path=%s;%s",
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
