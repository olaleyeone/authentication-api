package com.olaleyeone.auth.service;

import com.google.gson.Gson;
import com.olaleyeone.auth.data.SimpleAccessClaims;
import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.security.data.AccessClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Named
public class BaseJwtService {

    private final SigningKeyResolverImpl signingKeyResolver;
    private final Gson gson;

    private JwtParser jwtParser;

    private Key key;
    private String keyId;

    @PostConstruct
    public void init() {
        jwtParser = Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build();
    }

    public boolean hasKey() {
        return keyId != null;
    }

    protected void updateKey(Map.Entry<Key, SignatureKey> keyEntry) {
        key = keyEntry.getKey();
        keyId = keyEntry.getValue().getKeyId();
        signingKeyResolver.registerKey(keyEntry.getValue());
    }

    public String createJwt(RefreshToken refreshToken, Instant expiryInstant) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
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

    public AccessClaims parseAccessToken(String jws) {
        Claims claims = jwtParser.parseClaimsJws(jws).getBody();
        return new SimpleAccessClaims(claims, gson);
    }

}
