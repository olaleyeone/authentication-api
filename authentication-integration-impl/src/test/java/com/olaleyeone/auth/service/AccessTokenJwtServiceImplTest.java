package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AccessTokenJwtServiceImplTest extends ComponentTest {

    private AccessTokenJwtServiceImpl jwtService;

    private PortalUser portalUser;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        jwtService = new AccessTokenJwtServiceImpl(Keys.secretKeyFor(SignatureAlgorithm.HS256), new Gson());

        refreshToken = JwtServiceImplTestHelper.refreshToken();
        portalUser = refreshToken.getPortalUser();
    }

    @Test
    void getAccessToken() {
        long accessTokenDurationInSeconds = 20;
        refreshToken.setAccessExpiresAt(LocalDateTime.now().plusSeconds(accessTokenDurationInSeconds));

        String jws = jwtService.generateJwt(refreshToken).getToken();
        assertNotNull(jws);

        AccessClaims accessClaims = jwtService.parseAccessToken(jws);
        assertEquals(refreshToken.getId().toString(), accessClaims.getId());
        assertEquals(portalUser.getId().toString(), accessClaims.getSubject());
    }

    @Test
    void shouldFailForExpiredAccessToken() {
        long accessTokenDurationInSeconds = -20;
        refreshToken.setAccessExpiresAt(LocalDateTime.now().plusSeconds(accessTokenDurationInSeconds));
        String jws = jwtService.generateJwt(refreshToken).getToken();
        assertNotNull(jws);
        assertThrows(ExpiredJwtException.class, () -> jwtService.parseAccessToken(jws));
    }
}