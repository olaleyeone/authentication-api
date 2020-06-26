package com.olaleyeone.auth.integration.security.passwordreset;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.integration.security.PasswordResetTokenGenerator;
import com.olaleyeone.auth.integration.security.SimpleSigningKeyResolver;
import com.olaleyeone.auth.service.KeyGenerator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Map;

@RequiredArgsConstructor
@Builder
public class PasswordResetTokenGeneratorImpl implements PasswordResetTokenGenerator {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;

    private final SimpleSigningKeyResolver signingKeyResolver;
    private final PasswordResetJwsGenerator jwsGenerator;

    @PostConstruct
    public void init() {
        if (jwsGenerator.hasKey()) {
            logger.warn("Prevented duplicate initialization");
            return;
        }
        taskContextFactory.startBackgroundTask(
                "INITIALIZE PASSWORD RESET TOKEN KEY",
                null,
                () -> {
                    Map.Entry<Key, SignatureKey> keyEntry = keyGenerator.generateKey(JwtTokenType.PASSWORD_RESET);
                    jwsGenerator.updateKey(keyEntry);
                    signingKeyResolver.addKey(keyEntry.getValue());
                });
    }

    @Override
    public JwtDto generateJwt(PasswordResetRequest passwordResetRequest) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(passwordResetRequest.getSecondsTillExpiry());
        jwtDto.setToken(jwsGenerator.createJwt(passwordResetRequest, passwordResetRequest.getExpiryInstant()));
        return jwtDto;
    }

}
