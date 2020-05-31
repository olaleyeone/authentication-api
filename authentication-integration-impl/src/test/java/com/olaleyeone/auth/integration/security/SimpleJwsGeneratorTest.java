package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SimpleJwsGeneratorTest extends ComponentTest {

    private SimpleJwsGenerator jwsGenerator;

    private KeyPair keyPair;
    private SignatureKey signatureKey;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        jwsGenerator = new SimpleJwsGenerator();

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();

        signatureKey = new SignatureKey();
        signatureKey.setKeyId(faker.idNumber().ssnValid());
    }

    @Test
    void hasKeyWithNoValueSet() {
        assertFalse(jwsGenerator.hasKey());
    }

    @Test
    void updateKey() {
        SignatureKey signatureKey = new SignatureKey();
        signatureKey.setKeyId(faker.idNumber().ssnValid());
        jwsGenerator.updateKey(Pair.of(null, signatureKey));
        assertTrue(jwsGenerator.hasKey());
    }

    @Test
    void createJwt() {

        jwsGenerator.updateKey(Pair.of(keyPair.getPrivate(), signatureKey));

        RefreshToken refreshToken = getRefreshToken();

        Instant expiryInstant = Instant.now().plusSeconds(60);
        String jwt = jwsGenerator.createJwt(refreshToken, expiryInstant);

        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build();

        Jws<Claims> accessClaims = jwtParser.parseClaimsJws(jwt);

        assertEquals(accessClaims.getBody().getId(), refreshToken.getId().toString());
        assertEquals(accessClaims.getBody().getSubject(), refreshToken.getPortalUser().getId().toString());
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