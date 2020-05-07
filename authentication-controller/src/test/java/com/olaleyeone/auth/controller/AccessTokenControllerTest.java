package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.integration.auth.JwtService;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.repository.RefreshTokenRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.security.access.AccessStatus;
import com.olaleyeone.auth.security.authorizer.NotClientTokenAuthorizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccessTokenControllerTest extends ControllerTest {

    @Autowired
    private NotClientTokenAuthorizer authorizer;

    @Autowired
    private AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    @JwtToken(JwtTokenType.REFRESH)
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        Mockito.doReturn(AccessStatus.allowed()).when(authorizer).getStatus(Mockito.any(), Mockito.any());
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getSubject();
        Mockito.doReturn(String.valueOf(faker.number().randomNumber())).when(accessClaims)
                .getId();
    }

    @Test
    void getUserDetails() throws Exception {
        AccessTokenApiResponse accessTokenApiResponse = new AccessTokenApiResponse();
        accessTokenApiResponse.setId(faker.number().randomNumber());

        RefreshToken refreshToken = new RefreshToken();
        Mockito.doReturn(accessClaims).when(jwtService).parseToken(Mockito.any());
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findActiveToken(Mockito.any());
        Mockito.doReturn(new HttpEntity<>(accessTokenApiResponse)).when(accessTokenApiResponseHandler).getAccessToken(Mockito.any(RefreshToken.class));

        String token = UUID.randomUUID().toString();
        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(getCookie(token)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    AccessTokenApiResponse response = objectMapper.readValue(result.getResponse().getContentAsByteArray(), AccessTokenApiResponse.class);
                    assertNotNull(response);
                    assertEquals(accessTokenApiResponse.getId(), response.getId());
                });
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findActiveToken(Long.valueOf(accessClaims.getId()));
        Mockito.verify(accessTokenApiResponseHandler, Mockito.times(1))
                .getAccessToken(refreshToken);
        Mockito.verify(jwtService, Mockito.times(1))
                .parseToken(token);
    }

    @Test
    void shouldReturnUnauthorizedForEmptyCookies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForInactiveUser() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.doReturn(accessClaims).when(jwtService).parseToken(Mockito.any());
        Mockito.doReturn(Optional.empty()).when(refreshTokenRepository).findActiveToken(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(getCookie(token)))
                .andExpect(status().isUnauthorized());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findActiveToken(Long.valueOf(accessClaims.getId()));
        Mockito.verify(jwtService, Mockito.times(1))
                .parseToken(token);
    }

    @Test
    void shouldValidateToken() throws Exception {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken();
        Mockito.doReturn(accessClaims).when(jwtService).parseToken(Mockito.any());
        Mockito.doReturn(Optional.of(refreshToken)).when(refreshTokenRepository).findActiveToken(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(getCookie(token)))
                .andExpect(status().isOk());
        Mockito.verify(refreshTokenRepository, Mockito.times(1))
                .findActiveToken(Long.valueOf(accessClaims.getId()));
        Mockito.verify(jwtService, Mockito.times(1))
                .parseToken(token);
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        String token = UUID.randomUUID().toString();
        Mockito.doReturn(null).when(accessClaimsExtractor).getClaims(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(getCookie(token)))
                .andExpect(status().isUnauthorized());
        Mockito.verify(jwtService, Mockito.times(1))
                .parseToken(token);
    }

    @Test
    void shouldRejectNonHttpCookie() throws Exception {
        Cookie cookie = new Cookie(faker.internet().domainWord(), UUID.randomUUID().toString());
        cookie.setHttpOnly(false);
        cookie.setSecure(true);

        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(cookie))
                .andExpect(status().isUnauthorized());
        Mockito.verify(jwtService, Mockito.never())
                .parseToken(Mockito.any());
    }

    @Test
    void shouldRejectNonSecureCookie() throws Exception {
        Cookie cookie = new Cookie(faker.internet().domainWord(), UUID.randomUUID().toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(false);

        mockMvc.perform(MockMvcRequestBuilders.post(AccessTokenApiResponseHandler.TOKEN_ENDPOINT)
                .cookie(cookie))
                .andExpect(status().isUnauthorized());
        Mockito.verify(jwtService, Mockito.never())
                .parseToken(Mockito.any());
    }

    private Cookie getCookie(String token) {
        Cookie cookie = new Cookie(AccessTokenApiResponseHandler.REFRESH_TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        return cookie;
    }
}