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
public class AccessTokenJwtServiceImpl implements JwtService {

    private final KeyGenerator keyGenerator;
    private final TaskContextFactory taskContextFactory;
    private final BaseJwtService baseJwtService;

    @PostConstruct
    public void init() {
        taskContextFactory.startBackgroundTask("INITIALIZE ACCESS TOKEN KEY", null, ()->{
            baseJwtService.updateKey(keyGenerator.generateKey());
        });
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillAccessExpiry());
        jwtDto.setToken(baseJwtService.createJwt(refreshToken, refreshToken.getAccessExpiryInstant()));
        return jwtDto;
    }

    @Override
    public AccessClaims parseToken(String jws) {
        return baseJwtService.parseAccessToken(jws);
    }

}
