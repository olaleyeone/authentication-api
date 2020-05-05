package com.olaleyeone.auth.integration.auth;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.audittrail.impl.TaskContextImpl;
import com.olaleyeone.audittrail.impl.TaskContextSaver;
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
    private TaskContextSaver taskContextSaver;
    @Mock
    private BaseJwtService baseJwtService;

    @BeforeEach
    public void setUp() {
        jwtService = RefreshTokenJwtServiceImpl.builder()
                .baseJwtService(baseJwtService)
                .keyGenerator(keyGenerator)
                .taskContextFactory(taskContextFactory)
                .taskContextSaver(taskContextSaver)
                .build();

        refreshToken = JwtServiceImplTestHelper.refreshToken();
    }

    @Test
    public void shouldInitializeKey() {
        Pair<Key, SignatureKey> key = Pair.of(null, null);
        TaskContextImpl taskContext = Mockito.mock(TaskContextImpl.class);
        Mockito.doReturn(key)
                .when(keyGenerator)
                .generateKey();
        Mockito.doReturn(taskContext)
                .when(taskContextFactory)
                .start(Mockito.any());
        jwtService.init();
        Mockito.verify(keyGenerator, Mockito.times(1))
                .generateKey();
        Mockito.verify(baseJwtService, Mockito.times(1))
                .updateKey(key);
        Mockito.verify(taskContextFactory, Mockito.times(1))
                .start(Mockito.any());
        Mockito.verify(taskContextSaver, Mockito.times(1))
                .save(taskContext);
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
        AccessClaims actual = jwtService.parseAccessToken(jws);
        assertSame(expected, actual);
        Mockito.verify(baseJwtService, Mockito.times(1))
                .parseAccessToken(jws);
    }
}