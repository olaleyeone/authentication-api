package com.olaleyeone.auth.integration.security.auth;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.google.gson.Gson;
import com.olaleyeone.auth.integration.security.auth.AccessClaimsExtractorImpl;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AccessClaimsExtractorImplTest extends ComponentTest {

    @Mock
    private SigningKeyResolver signingKeyResolver;

    private Gson gson;

    private KeyPair keyPair;

    private AccessClaimsExtractorImpl accessClaimsExtractor;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        gson = new Gson();
        accessClaimsExtractor = new AccessClaimsExtractorImpl(signingKeyResolver, gson);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();

    }

    @Test
    void getClaims() {
        Mockito.doReturn(keyPair.getPublic()).when(signingKeyResolver).resolveSigningKey(Mockito.any(), Mockito.any(Claims.class));

        Instant now = Instant.now();
        String id = faker.idNumber().valid();
        String kid = faker.idNumber().valid();
        String subject = faker.idNumber().valid();

        String jws = Jwts.builder()
                .setHeaderParam("kid", kid)
                .setId(id)
                .setSubject(subject)
                .setIssuer("doorbell")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(5)))
                .signWith(keyPair.getPrivate())
                .compact();

        AccessClaims claims = accessClaimsExtractor.getClaims(jws);
        assertNotNull(claims);
        assertEquals(id, claims.getId());
        assertEquals(subject, claims.getSubject());
        Mockito.verify(signingKeyResolver, Mockito.times(1)).resolveSigningKey(Mockito.argThat(argument -> {
            assertEquals(kid, argument.getKeyId());
            return true;
        }), Mockito.any(Claims.class));
    }

    @Test
    void rejectNoneAlgorithm() {
        Instant now = Instant.now();

        String jws = Jwts.builder()
                .setIssuer("doorbell")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(5)))
                .compact();

        assertThrows(UnsupportedJwtException.class, () -> accessClaimsExtractor.getClaims(jws));
    }
}