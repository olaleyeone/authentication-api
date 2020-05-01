package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BaseJwtServiceTest extends ComponentTest {

    private final Gson gson = new Gson();
    @Mock
    private SigningKeyResolverImpl signingKeyResolver;

    private BaseJwtService baseJwtService;

    private KeyPair keyPair;
    private SignatureKey signatureKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        baseJwtService = new BaseJwtService(signingKeyResolver, gson);
        baseJwtService.init();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();


        signatureKey = new SignatureKey();
        signatureKey.setKeyId(faker.idNumber().ssnValid());
    }

    @Test
    void hasKeyWithNoValueSet() {
        assertFalse(baseJwtService.hasKey());
    }

    @Test
    void updateKey() {
        SignatureKey signatureKey = new SignatureKey();
        signatureKey.setKeyId(faker.idNumber().ssnValid());
        baseJwtService.updateKey(Pair.of(null, signatureKey));
        assertTrue(baseJwtService.hasKey());
    }

    @Test
    void createJwt() {

        baseJwtService.updateKey(Pair.of(keyPair.getPrivate(), signatureKey));

        RefreshToken refreshToken = getRefreshToken();

        Instant expiryInstant = Instant.now().plusSeconds(60);
        String jwt = baseJwtService.createJwt(refreshToken, expiryInstant);

        Mockito.doReturn(keyPair.getPublic()).when(signingKeyResolver)
                .resolveSigningKey(Mockito.any(JwsHeader.class), Mockito.any(Claims.class));
        AccessClaims accessClaims = baseJwtService.parseAccessToken(jwt);
        Mockito.verify(signingKeyResolver, Mockito.times(1))
                .resolveSigningKey(Mockito.argThat(argument -> {
                    assertEquals(argument.getKeyId(), signatureKey.getKeyId());
                    return true;
                }), Mockito.any(Claims.class));

        assertEquals(accessClaims.getId(), refreshToken.getId().toString());
        assertEquals(accessClaims.getSubject(), refreshToken.getPortalUser().getId().toString());
    }

    @Test
    void parseExpiredToken() {

        baseJwtService.updateKey(Pair.of(keyPair.getPrivate(), signatureKey));

        RefreshToken refreshToken = getRefreshToken();

        Instant expiryInstant = Instant.now().minusSeconds(60);
        String jwt = baseJwtService.createJwt(refreshToken, expiryInstant);

        Mockito.doReturn(keyPair.getPublic()).when(signingKeyResolver)
                .resolveSigningKey(Mockito.any(JwsHeader.class), Mockito.any(Claims.class));
        assertThrows(ExpiredJwtException.class, () -> baseJwtService.parseAccessToken(jwt));
    }

    private RefreshToken getRefreshToken() {
        PortalUser portalUser = new PortalUser();
        portalUser.setId(faker.number().randomNumber());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(faker.number().randomNumber());
        refreshToken = Mockito.spy(refreshToken);
        Mockito.doReturn(portalUser).when(refreshToken).getPortalUser();
        return refreshToken;
    }
}