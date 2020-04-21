package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
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

class RefreshTokenJwtServiceImplTest extends ComponentTest {

    private RefreshTokenJwtServiceImpl jwtService;

    private PortalUser portalUser;
    private PortalUserAuthentication userAuthentication;
    private RefreshToken refreshToken;

    @BeforeEach
    public void setUp() {
        jwtService = new RefreshTokenJwtServiceImpl(Keys.secretKeyFor(SignatureAlgorithm.HS256), new Gson());
        portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());
        userAuthentication = new PortalUserAuthentication();
        userAuthentication.setPortalUser(portalUser);

        refreshToken = new RefreshToken();
        refreshToken.setId(faker.number().randomNumber());
        refreshToken.setActualAuthentication(userAuthentication);
        refreshToken.setPortalUser();
    }

    @Test
    void getRefreshToken() {
        refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(1));
        String jws = jwtService.generateJwt(refreshToken).getToken();
        assertNotNull(jws);
        AccessClaims accessClaims = jwtService.parseAccessToken(jws);
        assertEquals(refreshToken.getId().toString(), accessClaims.getId());
        assertEquals(portalUser.getId().toString(), accessClaims.getSubject());
    }

    @Test
    void shouldFailForExpiredRefreshToken() {
        refreshToken.setExpiresAt(LocalDateTime.now());
        String jws = jwtService.generateJwt(refreshToken).getToken();
        assertNotNull(jws);
        assertThrows(ExpiredJwtException.class, () -> jwtService.parseAccessToken(jws));
    }
}