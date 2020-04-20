package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.AccessTokenDto;
import com.olaleyeone.auth.qualifier.JwtEncryptionKey;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static String ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS = "ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS";

    @JwtEncryptionKey
    private final Key key;
    private final SettingService settingService;

    @Override
    public String getRefreshToken(RefreshToken refreshToken) {
        Instant now = Instant.now();
        return Jwts.builder().setSubject(refreshToken.getId().toString())
                .setNotBefore(Date.from(now))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(refreshToken.getExpiryInstant()))
                .signWith(key).compact();
    }

    @Override
    public AccessTokenDto getAccessToken(RefreshToken refreshToken) {
        AccessTokenDto tokenDto = new AccessTokenDto();
        tokenDto.setSecondsTillExpiry(settingService.getLong(ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS, 300));

        Instant now = Instant.now();
        Instant expiryInstant = now.plusSeconds(tokenDto.getSecondsTillExpiry());

//        jti, iss, sub, aud, iat, nbf, exp
        tokenDto.setToken(Jwts.builder()
                .setId(refreshToken.getId().toString())
                .setIssuer("doorbell")
                .setSubject(refreshToken.getPortalUser().getId().toString())
//                .setAudience("all")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(key).compact());
        return tokenDto;
    }

    @Override
    public String getSubject(String jws) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody().getSubject();
    }
}
