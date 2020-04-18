package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.qualifier.JwtEncryptionKey;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    public static String ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS = "ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS";

    @JwtEncryptionKey
    private final Key key;
    private final SettingService settingService;

    @Override
    public String getRefreshToken(RefreshToken refreshToken) {
        Instant expiryInstant = refreshToken.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant();
        return Jwts.builder().setSubject(refreshToken.getId().toString())
                .setExpiration(Date.from(expiryInstant))
                .signWith(key).compact();
    }

    @Override
    public String getAccessToken(PortalUser portalUser) {
        Instant expiryInstant = Instant.now().plusSeconds(settingService.getInteger(ACCESS_TOKEN_EXPIRY_DURATION_IN_SECONDS, 300));
        return Jwts.builder().setSubject(portalUser.getId().toString())
                .setExpiration(Date.from(expiryInstant))
                .signWith(key).compact();
    }

    @Override
    public String getSubject(String jws) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody().getSubject();
    }
}
