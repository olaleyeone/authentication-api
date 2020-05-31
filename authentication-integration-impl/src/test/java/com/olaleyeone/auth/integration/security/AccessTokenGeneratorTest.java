package com.olaleyeone.auth.integration.security;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.service.KeyGenerator;
import com.olaleyeone.auth.test.ComponentTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.Key;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccessTokenGeneratorTest extends ComponentTest {

    private AccessTokenGenerator accessTokenGenerator;

    private RefreshToken refreshToken;

    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private SimpleJwsGenerator jwsGenerator;
    @Mock
    private SimpleSigningKeyResolver signingKeyResolver;

    @BeforeEach
    public void setUp() {
        accessTokenGenerator = AccessTokenGenerator.builder()
                .jwsGenerator(jwsGenerator)
                .keyGenerator(keyGenerator)
                .taskContextFactory(taskContextFactory)
                .signingKeyResolver(signingKeyResolver)
                .build();

        refreshToken = JwtServiceImplTestHelper.refreshToken();
    }

    @Test
    public void shouldInitializeKey() {
        Mockito.doAnswer(invocation -> {
            Action action = invocation.getArgument(2);
            action.execute();
            return null;
        }).when(taskContextFactory).startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());

        Pair<Key, SignatureKey> key = Pair.of(null, null);
        Mockito.doReturn(key)
                .when(keyGenerator)
                .generateKey(Mockito.any());
        accessTokenGenerator.init();
        Mockito.verify(keyGenerator, Mockito.times(1))
                .generateKey(JwtTokenType.ACCESS);
        Mockito.verify(jwsGenerator, Mockito.times(1))
                .updateKey(key);
        Mockito.verify(signingKeyResolver, Mockito.times(1))
                .addKey(key.getValue());
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldPreventDuplicateKeyInitialization() {
        Mockito.doReturn(true).when(jwsGenerator).hasKey();
        accessTokenGenerator.init();
        Mockito.verify(keyGenerator, Mockito.never())
                .generateKey(Mockito.any());
        Mockito.verify(taskContextFactory, Mockito.never())
                .startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getAccessToken() {
        long accessTokenDurationInSeconds = 20;
        refreshToken.setAccessExpiresAt(LocalDateTime.now().plusSeconds(accessTokenDurationInSeconds));

        String jws = faker.buffy().quotes();
        Mockito.doReturn(jws).when(jwsGenerator)
                .createJwt(Mockito.any(), Mockito.any());

        String actual = accessTokenGenerator.generateJwt(refreshToken).getToken();
        assertEquals(jws, actual);

        Mockito.verify(jwsGenerator, Mockito.times(1))
                .createJwt(Mockito.any(), Mockito.any());
    }
}