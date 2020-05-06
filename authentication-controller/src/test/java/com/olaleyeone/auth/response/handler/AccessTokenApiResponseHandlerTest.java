package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.integration.auth.JwtService;
import com.olaleyeone.auth.response.pojo.UserApiResponse;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.net.HttpCookie;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenApiResponseHandlerTest extends ComponentTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AccessTokenApiResponseHandler handler;

    private PortalUser user;
    private RefreshToken refreshToken;
    private PortalUserAuthentication userAuthentication;

    private JwtDto refreshJwt;
    private JwtDto accessJwt;

    @BeforeEach
    void setUp() {

        user = new PortalUser();
        user.setFirstName(faker.name().firstName());
        user.setLastName(faker.name().lastName());

        PortalUserIdentifier userIdentifier = new PortalUserIdentifier();
        userIdentifier.setPortalUser(user);

        userAuthentication = new PortalUserAuthentication();
        userAuthentication.setResponseType(AuthenticationResponseType.SUCCESSFUL);
        userAuthentication.setPortalUserIdentifier(userIdentifier);
        userAuthentication.setPortalUser(userIdentifier.getPortalUser());

        refreshToken = new RefreshToken();
        refreshToken.setActualAuthentication(userAuthentication);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        refreshJwt = JwtDto.builder()
                .token(UUID.randomUUID().toString())
                .secondsTillExpiry(faker.number().randomNumber())
                .build();
        accessJwt = refreshJwt;


        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any(PortalUserAuthentication.class)))
                .then(invocation -> refreshToken);
        Mockito.when(jwtService.generateJwt(Mockito.any()))
                .then(invocation -> refreshJwt);
        Mockito.when(jwtService.generateJwt(Mockito.any()))
                .then(invocation -> accessJwt);
    }

    @Test
    public void getUserPojoForAuthentication() {
        HttpEntity<UserApiResponse> responseEntity = handler.getAccessToken(userAuthentication);
        UserApiResponse userApiResponse = responseEntity.getBody();
        assertNotNull(userApiResponse);
        assertEquals(user.getFirstName(), userApiResponse.getFirstName());
        assertEquals(user.getLastName(), userApiResponse.getLastName());
        assertNull(userApiResponse.getRefreshToken());
        assertNull(userApiResponse.getAccessToken());
    }

    @Test
    public void getUserPojoForRefreshToken() {
        HttpEntity<UserApiResponse> responseEntity = handler.getAccessToken(refreshToken);
        UserApiResponse userApiResponse = responseEntity.getBody();
        assertNotNull(userApiResponse);
        assertEquals(user.getFirstName(), userApiResponse.getFirstName());
        assertEquals(user.getLastName(), userApiResponse.getLastName());
        assertNull(userApiResponse.getRefreshToken());
        assertNull(userApiResponse.getAccessToken());
    }

    @Test
    public void shouldReturnCacheHeaders() {
        HttpEntity<UserApiResponse> responseEntity = handler.getAccessToken(userAuthentication);
        assertEquals("no-store", responseEntity.getHeaders().getCacheControl());
        assertEquals("no-cache", responseEntity.getHeaders().getPragma());
    }

    @Test
    public void shouldReturnAccessTokenCookies() {

        HttpEntity<UserApiResponse> responseEntity = handler.getAccessToken(userAuthentication);

        List<HttpCookie> httpCookies = getCookiesByName(responseEntity, "access_token");
        assertEquals(1, httpCookies.size());
        HttpCookie httpCookie = httpCookies.iterator().next();
        assertEquals(accessJwt.getSecondsTillExpiry(), httpCookie.getMaxAge());
        assertSecure(httpCookie);
    }

    @Test
    public void shouldReturnRefreshTokenCookies() {
        HttpEntity<UserApiResponse> responseEntity = handler.getAccessToken(userAuthentication);

        List<HttpCookie> httpCookies = getCookiesByName(responseEntity, "refresh_token");
        assertEquals(1, httpCookies.size());
        HttpCookie httpCookie = httpCookies.iterator().next();
        assertTrue((refreshToken.getSecondsTillExpiry() - httpCookie.getMaxAge()) <= 1);
        assertSecure(httpCookie);
    }

    private List<HttpCookie> getCookiesByName(HttpEntity<?> responseEntity, String name) {
        return HttpCookie.parse(collectCookies(responseEntity))
                .stream().filter(httpCookie -> httpCookie.getName().equals(name))
                .collect(Collectors.toList());
    }

    private String collectCookies(HttpEntity<?> responseEntity) {
        List<String> list = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(list);
        return String.join(", ", list);
    }

    private void assertSecure(HttpCookie httpCookie) {
        assertTrue(httpCookie.getSecure());
        assertTrue(httpCookie.isHttpOnly());
    }
}