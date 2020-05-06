package com.olaleyeone.auth.integration.auth;

import com.olaleyeone.audittrail.impl.TaskContextFactory;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.security.data.AccessClaims;
import com.olaleyeone.auth.service.KeyGenerator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Builder
public class RefreshTokenJwtServiceImpl implements JwtService {

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;
    private final BaseJwtService baseJwtService;

    @PostConstruct
    public void init() {
        taskContextFactory.startBackgroundTask("INITIALIZE REFRESH TOKEN KEY", null, ()->{
            baseJwtService.updateKey(keyGenerator.generateKey());
        });
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillExpiry());
        jwtDto.setToken(baseJwtService.createJwt(refreshToken, refreshToken.getExpiryInstant()));
        return jwtDto;
    }

    @Override
    public AccessClaims parseAccessToken(String jws) {
        return baseJwtService.parseAccessToken(jws);
    }

}
