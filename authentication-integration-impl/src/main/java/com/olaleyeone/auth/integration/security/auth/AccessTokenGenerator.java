package com.olaleyeone.auth.integration.security.auth;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.integration.security.AuthTokenGenerator;
import com.olaleyeone.auth.integration.security.SimpleSigningKeyResolver;
import com.olaleyeone.auth.service.KeyGenerator;
import com.olaleyeone.auth.service.SettingService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Map;

@RequiredArgsConstructor
@Builder
public class AccessTokenGenerator implements AuthTokenGenerator {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;

    private final SimpleSigningKeyResolver signingKeyResolver;
    private final AuthJwsGenerator jwsGenerator;
    private final SettingService settingService;

    @PostConstruct
    public void init() {
        if (jwsGenerator.hasKey()) {
            logger.warn("Prevented duplicate initialization");
            return;
        }
        taskContextFactory.startBackgroundTask(
                "INITIALIZE ACCESS TOKEN KEY",
                null,
                () -> {
                    Map.Entry<Key, SignatureKey> keyEntry = keyGenerator.generateKey(JwtTokenType.ACCESS);
                    jwsGenerator.updateKey(keyEntry);
                    signingKeyResolver.addKey(keyEntry.getValue());
                });
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillAccessExpiry());
        Integer access_token_clock_skew = settingService.getInteger("ACCESS_TOKEN_CLOCK_SKEW", 2);
        jwtDto.setToken(jwsGenerator.createJwt(refreshToken, refreshToken.getAccessExpiryInstant().plusSeconds(access_token_clock_skew)));
        return jwtDto;
    }

}
