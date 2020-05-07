package com.olaleyeone.auth.integration.auth;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.security.data.AccessClaims;
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
import static org.junit.jupiter.api.Assertions.assertSame;

class RefreshTokenJwtServiceImplTest extends ComponentTest {

    private RefreshTokenJwtServiceImpl jwtService;

    private RefreshToken refreshToken;

    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private TaskContextFactory taskContextFactory;
    @Mock
    private BaseJwtService baseJwtService;

    @BeforeEach
    public void setUp() {
        jwtService = RefreshTokenJwtServiceImpl.builder()
                .baseJwtService(baseJwtService)
                .keyGenerator(keyGenerator)
                .taskContextFactory(taskContextFactory)
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
                .generateKey();
        jwtService.init();
        Mockito.verify(keyGenerator, Mockito.times(1))
                .generateKey();
        Mockito.verify(baseJwtService, Mockito.times(1))
                .updateKey(key);
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void getRefreshToken() {
        refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(1));

        String jws = faker.buffy().quotes();
        Mockito.doReturn(jws).when(baseJwtService)
                .createJwt(Mockito.any(), Mockito.any());

        String actual = jwtService.generateJwt(refreshToken).getToken();
        assertEquals(jws, actual);

        Mockito.verify(baseJwtService, Mockito.times(1))
                .createJwt(Mockito.any(), Mockito.any());
    }

    @Test
    void parseAccessToken() {
        AccessClaims expected = Mockito.mock(AccessClaims.class);
        Mockito.doReturn(expected).when(baseJwtService)
                .parseAccessToken(Mockito.any());

        String jws = faker.buffy().quotes();
        AccessClaims actual = jwtService.parseToken(jws);
        assertSame(expected, actual);
        Mockito.verify(baseJwtService, Mockito.times(1))
                .parseAccessToken(jws);
    }
}