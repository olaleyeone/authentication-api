package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.SimpleAccessClaims;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.security.data.AccessClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public abstract class BaseJwtService {

    private final Gson gson;

    protected String createJwt(RefreshToken refreshToken, Key key, Instant now, Instant expiryInstant) {
        return Jwts.builder()
                .setId(refreshToken.getId().toString())
                .setSubject(refreshToken.getPortalUser().getId().toString())
                .setIssuer("doorbell")
//                .setAudience("all")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(key)
                .compact();
    }

    public AccessClaims parseAccessToken(String jws, Key key) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody();
        return new SimpleAccessClaims(claims, gson);
    }
}
