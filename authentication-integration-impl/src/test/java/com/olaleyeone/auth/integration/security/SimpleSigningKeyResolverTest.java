package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.repository.SignatureKeyRepository;
import com.olaleyeone.auth.test.ComponentTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleSigningKeyResolverTest extends ComponentTest {

    @Mock
    private SignatureKeyRepository signatureKeyRepository;

    private SimpleSigningKeyResolver signingKeyResolver;
    private KeyPair keyPair;
    private SignatureKey signatureKey;

    private JwtTokenType jwtTokenType = JwtTokenType.ACCESS;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        signingKeyResolver = new SimpleSigningKeyResolver(signatureKeyRepository, jwtTokenType);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();

        signatureKey = createSignatureKey();
    }

    private SignatureKey createSignatureKey() {
        SignatureKey signatureKey = new SignatureKey();
        signatureKey.setKeyId(faker.idNumber().ssnValid());
        signatureKey.setCreatedOn(LocalDateTime.now());
        signatureKey.setAlgorithm(keyPair.getPublic().getAlgorithm());
        signatureKey.setEncodedKey(keyPair.getPublic().getEncoded());
        return signatureKey;
    }

    @Test
    void registerKey() {

        signingKeyResolver.addKey(signatureKey);
        JwsHeader jwsHeader = Jwts.jwsHeader();
        jwsHeader.setKeyId(signatureKey.getKeyId());
        Key key = signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        assertNotNull(key);
        assertArrayEquals(signatureKey.getEncodedKey(), key.getEncoded());
        Mockito.verify(signatureKeyRepository, Mockito.never())
                .findByKeyIdAndType(Mockito.any(), Mockito.any());
    }

    @Test
    void resolveSigningKey() {

        Mockito.doReturn(Optional.of(signatureKey))
                .when(signatureKeyRepository)
                .findByKeyIdAndType(Mockito.any(), Mockito.any());

        JwsHeader jwsHeader = Jwts.jwsHeader();
        jwsHeader.setKeyId(signatureKey.getKeyId());
        Key key = signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        assertNotNull(key);
        assertArrayEquals(signatureKey.getEncodedKey(), key.getEncoded());
        Mockito.verify(signatureKeyRepository, Mockito.times(1))
                .findByKeyIdAndType(signatureKey.getKeyId(), jwtTokenType);
    }

    @Test
    void shouldCacheKeys() {

        Mockito.doReturn(Optional.of(signatureKey))
                .when(signatureKeyRepository)
                .findByKeyIdAndType(Mockito.any(), Mockito.any());

        JwsHeader jwsHeader = Jwts.jwsHeader();
        jwsHeader.setKeyId(signatureKey.getKeyId());
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        Mockito.verify(signatureKeyRepository, Mockito.times(1))
                .findByKeyIdAndType(signatureKey.getKeyId(), jwtTokenType);
    }

    @Test
    void shouldCacheKeysUpToMax() {
        signingKeyResolver.setMaxSize(2);

        for (int i = 0; i < signingKeyResolver.getMaxSize(); i++) {
            SignatureKey signatureKey = createSignatureKey();
            signatureKey.setCreatedOn(LocalDateTime.now().plusDays(i + 1));
            Mockito.doReturn(Optional.of(signatureKey))
                    .when(signatureKeyRepository)
                    .findByKeyIdAndType(Mockito.any(), Mockito.any());
            JwsHeader jwsHeader = Jwts.jwsHeader();
            jwsHeader.setKeyId(signatureKey.getKeyId());
            signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        }

        Mockito.reset(signatureKeyRepository);
        Mockito.doReturn(Optional.of(signatureKey))
                .when(signatureKeyRepository)
                .findByKeyIdAndType(Mockito.any(), Mockito.any());
        JwsHeader jwsHeader = Jwts.jwsHeader();
        jwsHeader.setKeyId(signatureKey.getKeyId());
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        Mockito.verify(signatureKeyRepository, Mockito.times(2))
                .findByKeyIdAndType(signatureKey.getKeyId(), jwtTokenType);
    }

    @Test
    void shouldReplaceStaleCacheKeys() {
        signingKeyResolver.setMaxSize(2);

        for (int i = 0; i < signingKeyResolver.getMaxSize(); i++) {
            SignatureKey signatureKey = createSignatureKey();
            signatureKey.setCreatedOn(LocalDateTime.now().minusDays(i + 1));
            Mockito.doReturn(Optional.of(signatureKey))
                    .when(signatureKeyRepository)
                    .findByKeyIdAndType(Mockito.any(), Mockito.any());
            JwsHeader jwsHeader = Jwts.jwsHeader();
            jwsHeader.setKeyId(signatureKey.getKeyId());
            signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        }

        Mockito.reset(signatureKeyRepository);
        Mockito.doReturn(Optional.of(signatureKey))
                .when(signatureKeyRepository)
                .findByKeyIdAndType(Mockito.any(), Mockito.any());
        JwsHeader jwsHeader = Jwts.jwsHeader();
        jwsHeader.setKeyId(signatureKey.getKeyId());
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        signingKeyResolver.resolveSigningKey(jwsHeader, Mockito.mock(Claims.class));
        Mockito.verify(signatureKeyRepository, Mockito.times(1))
                .findByKeyIdAndType(signatureKey.getKeyId(), jwtTokenType);
    }
}