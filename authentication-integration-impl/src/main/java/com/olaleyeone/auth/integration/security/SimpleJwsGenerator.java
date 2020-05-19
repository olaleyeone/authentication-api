package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.data.entity.SignatureKey;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class SimpleJwsGenerator {

    private Key key;
    private String keyId;

    public boolean hasKey() {
        return keyId != null;
    }

    protected void updateKey(Map.Entry<Key, SignatureKey> keyEntry) {
        key = keyEntry.getKey();
        keyId = keyEntry.getValue().getKeyId();
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

}
