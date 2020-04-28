package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.security.data.AccessClaims;

import javax.inject.Named;
import java.security.Key;
import java.time.Instant;

@Named
@JwtToken(JwtTokenType.REFRESH)
public class RefreshTokenJwtServiceImpl extends BaseJwtService implements JwtService {

    private final Key key;

    public RefreshTokenJwtServiceImpl(Key key, Gson gson) {
        super(gson);
        this.key = key;
    }

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillExpiry());

        Instant now = Instant.now();

        jwtDto.setToken(createJwt(refreshToken, key, now, refreshToken.getExpiryInstant()));
        return jwtDto;
    }

    @Override
    public AccessClaims parseAccessToken(String jws) {
        return super.parseAccessToken(jws, key);
    }

}
