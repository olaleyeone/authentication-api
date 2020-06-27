package com.olaleyeone.auth.integration.security.passwordreset;

import com.olaleyeone.audittrail.context.Action;
import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.integration.security.SimpleSigningKeyResolver;
import com.olaleyeone.auth.service.KeyGenerator;
import com.olaleyeone.auth.test.ComponentTest;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.security.Key;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenGeneratorImplTest extends ComponentTest {

    @Mock
    private KeyGenerator keyGenerator;
    @Mock
    private TaskContextFactory taskContextFactory;

    @Mock
    private SimpleSigningKeyResolver signingKeyResolver;
    @Mock
    private PasswordResetJwsGenerator jwsGenerator;

    @InjectMocks
    private PasswordResetTokenGeneratorImpl passwordResetTokenGenerator;

    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setExpiresOn(LocalDateTime.now().plusMinutes(1));
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
        passwordResetTokenGenerator.init();
        Mockito.verify(keyGenerator, Mockito.times(1))
                .generateKey(JwtTokenType.PASSWORD_RESET);
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
        passwordResetTokenGenerator.init();
        Mockito.verify(keyGenerator, Mockito.never())
                .generateKey(Mockito.any());
        Mockito.verify(taskContextFactory, Mockito.never())
                .startBackgroundTask(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void generateJwt() {
        String resetToken = faker.internet().password();
        Mockito.doReturn(resetToken).when(jwsGenerator).createJwt(Mockito.any(), Mockito.any());
        JwtDto generateJwt = passwordResetTokenGenerator.generateJwt(passwordResetRequest);
        assertEquals(resetToken, generateJwt.getToken());
    }
}