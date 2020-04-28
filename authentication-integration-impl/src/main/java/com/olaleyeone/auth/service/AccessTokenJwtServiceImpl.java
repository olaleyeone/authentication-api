package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.SimpleAccessClaims;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.qualifier.JwtTokenType;
import com.olaleyeone.auth.security.data.AccessClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.inject.Named;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Named
@JwtToken(JwtTokenType.ACCESS)
@RequiredArgsConstructor
public class AccessTokenJwtServiceImpl implements JwtService {

    private final Key key;
    private final SettingService settingService;
    private final Gson gson;

    @Override
    public JwtDto generateJwt(RefreshToken refreshToken) {
        JwtDto jwtDto = new JwtDto();
        jwtDto.setSecondsTillExpiry(refreshToken.getSecondsTillAccessExpiry());

        Instant now = Instant.now();
        Instant expiryInstant = now.plusSeconds(jwtDto.getSecondsTillExpiry());

//        jti, iss, sub, aud, iat, nbf, exp
        jwtDto.setToken(Jwts.builder()
                .setId(refreshToken.getId().toString())
                .setSubject(refreshToken.getPortalUser().getId().toString())
                .setIssuer("doorbell")
//                .setAudience("all")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(key).compact());
        return jwtDto;
    }

    @Override
    public AccessClaims parseAccessToken(String jws) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody();
        return new SimpleAccessClaims(claims, gson);
    }

}