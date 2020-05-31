package com.olaleyeone.auth.integration.security;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.dto.JwtDto;
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
public class RefreshTokenGenerator implements TokenGenerator {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;
    private final SimpleSigningKeyResolver signingKeyResolver;
    private final SimpleJwsGenerator jwsGenerator;

    @PostConstruct
    public void init() {
        if (jwsGenerator.hasKey()) {
            logger.warn("Prevented duplicate initialization");
            return;
        }
        taskContextFactory.startBackgroundTask(
                "INITIALIZE REFRESH TOKEN KEY",
                null,
                () -> {
                    Map.Entry<Key, SignatureKey> keyEntry = keyGenerator.generateKey(JwtTokenType.REFRESH);
                    jwsGenerator.updateKey(keyEntry);
                    signingKeyResolver.addKey(keyEntry.getValue());
                });
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillExpiry());
        jwtDto.setToken(jwsGenerator.createJwt(refreshToken, refreshToken.getExpiryInstant()));
        return jwtDto;
    }

}
