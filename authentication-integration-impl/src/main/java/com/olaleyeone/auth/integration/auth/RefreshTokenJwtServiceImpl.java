package com.olaleyeone.auth.integration.auth;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.service.KeyGenerator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Builder
public class RefreshTokenJwtServiceImpl implements JwtService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;
    private final BaseJwtService baseJwtService;

    @PostConstruct
    public void init() {
        if (baseJwtService.hasKey()) {
            logger.warn("Prevented duplicate initialization");
            return;
        }
        taskContextFactory.startBackgroundTask(
                "INITIALIZE REFRESH TOKEN KEY",
                null,
                () -> baseJwtService.updateKey(keyGenerator.generateKey()));
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillExpiry());
        jwtDto.setToken(baseJwtService.createJwt(refreshToken, refreshToken.getExpiryInstant()));
        return jwtDto;
    }

    @Override
    public AccessClaims parseToken(String jws) {
        return baseJwtService.parseAccessToken(jws);
    }

}
