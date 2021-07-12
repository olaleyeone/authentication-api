package com.olaleyeone.auth.response.handler;

import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.security.AuthTokenGenerator;
import com.olaleyeone.auth.repository.PortalUserDataRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.RefreshTokenService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.net.HttpCookie;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenApiResponseHandlerTest extends ComponentTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthTokenGenerator tokenGenerator;

    @Mock
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Mock
    private PortalUserDataRepository portalUserDataRepository;

    @Mock
    private ApplicationContext applicationContext;

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
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(1));

        refreshJwt = JwtDto.builder()
                .token(UUID.randomUUID().toString())
                .secondsTillExpiry(faker.number().randomNumber())
                .build();
        accessJwt = refreshJwt;


        Mockito.when(refreshTokenService.createRefreshToken(Mockito.any(PortalUserAuthentication.class)))
                .then(invocation -> refreshToken);
        Mockito.when(tokenGenerator.generateJwt(Mockito.any()))
                .then(invocation -> refreshJwt);
        Mockito.when(tokenGenerator.generateJwt(Mockito.any()))
                .then(invocation -> accessJwt);
    }

    @Test
    public void getUserPojoForAuthentication() {
        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(userAuthentication);
        AccessTokenApiResponse accessTokenApiResponse = responseEntity.getBody();
        assertNotNull(accessTokenApiResponse);
        assertEquals(user.getFirstName(), accessTokenApiResponse.getFirstName());
        assertEquals(user.getLastName(), accessTokenApiResponse.getLastName());
        assertNotNull(accessTokenApiResponse.getRefreshToken());
        assertNotNull(accessTokenApiResponse.getAccessToken());
    }

    @Test
    public void getUserPojoForRefreshToken() {
        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(refreshToken);
        AccessTokenApiResponse accessTokenApiResponse = responseEntity.getBody();
        assertNotNull(accessTokenApiResponse);
        assertEquals(user.getFirstName(), accessTokenApiResponse.getFirstName());
        assertEquals(user.getLastName(), accessTokenApiResponse.getLastName());
        assertNotNull(accessTokenApiResponse.getRefreshToken());
        assertNotNull(accessTokenApiResponse.getAccessToken());
    }

    @Test
    public void shouldReturnCacheHeaders() {
        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(userAuthentication);
        assertEquals("no-store", responseEntity.getHeaders().getCacheControl());
        assertEquals("no-cache", responseEntity.getHeaders().getPragma());
    }

    @Test
    public void shouldReturnIdentifiers() {
        Set<String> emails = new HashSet<>(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        Set<String> phoneNumbers = new HashSet<>(Arrays.asList(UUID.randomUUID().toString(), UUID.randomUUID().toString()));

        Iterator<String> emailIterator = emails.iterator();
        Iterator<String> phoneNumberIterator = phoneNumbers.iterator();

        Mockito.doReturn(Arrays.asList(
                getPortalUserIdentifier(emailIterator.next(), UserIdentifierType.EMAIL_ADDRESS),
                getPortalUserIdentifier(emailIterator.next(), UserIdentifierType.EMAIL_ADDRESS),
                getPortalUserIdentifier(phoneNumberIterator.next(), UserIdentifierType.PHONE_NUMBER),
                getPortalUserIdentifier(phoneNumberIterator.next(), UserIdentifierType.PHONE_NUMBER)
        )).when(portalUserIdentifierRepository).findByPortalUser(Mockito.any());

        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(refreshToken);

        assertNotNull(responseEntity.getBody());
        assertEquals(emails, responseEntity.getBody().getEmailAddresses());
        assertEquals(phoneNumbers, responseEntity.getBody().getPhoneNumbers());
    }

    @Test
    public void shouldReturnUserData() {
        Mockito.doReturn(Arrays.asList(dtoFactory.make(PortalUserData.class))).when(portalUserDataRepository).findByPortalUser(Mockito.any());

        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(refreshToken);

        assertNotNull(responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().getData().size());
    }

    @Test
    public void shouldReturnAccessTokenCookies() {

        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(userAuthentication);

        List<HttpCookie> httpCookies = getCookiesByName(responseEntity, "access_token");
        assertEquals(1, httpCookies.size());
        HttpCookie httpCookie = httpCookies.iterator().next();
        assertEquals(accessJwt.getSecondsTillExpiry(), httpCookie.getMaxAge());
        assertEquals("/", httpCookie.getPath());
        assertFalse(httpCookie.getSecure());
        assertFalse(httpCookie.isHttpOnly());
    }

    @Test
    public void shouldReturnRefreshTokenCookies() {
        String contextPath = "/" + faker.internet().slug();
        handler = AccessTokenApiResponseHandler.builder()
                .accessTokenJwtService(tokenGenerator)
                .refreshTokenJwtService(tokenGenerator)
                .refreshTokenService(refreshTokenService)
                .portalUserDataRepository(portalUserDataRepository)
                .portalUserIdentifierRepository(portalUserIdentifierRepository)
                .contextPath(contextPath)
                .cookieFlags("Secure; HttpOnly")
                .build();
        HttpEntity<AccessTokenApiResponse> responseEntity = handler.getAccessToken(userAuthentication);

        List<HttpCookie> httpCookies = getCookiesByName(responseEntity, "refresh_token");
        assertEquals(1, httpCookies.size());
        HttpCookie httpCookie = httpCookies.iterator().next();
        assertTrue((refreshToken.getSecondsTillExpiry() - httpCookie.getMaxAge()) <= 1);
        assertEquals(String.format("%s%s", contextPath, AccessTokenApiResponseHandler.TOKEN_ENDPOINT), httpCookie.getPath());
        assertTrue(httpCookie.getSecure());
        assertTrue(httpCookie.isHttpOnly());
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

    PortalUserIdentifier getPortalUserIdentifier(String identifier, UserIdentifierType identifierType) {
        PortalUserIdentifier portalUser = dtoFactory.make(PortalUserIdentifier.class);
        portalUser.setIdentifierType(identifierType);
        portalUser.setIdentifier(identifier);
        return portalUser;
    }
}