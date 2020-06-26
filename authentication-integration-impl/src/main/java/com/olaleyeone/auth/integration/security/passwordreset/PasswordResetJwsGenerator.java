package com.olaleyeone.auth.integration.security.passwordreset;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class PasswordResetJwsGenerator {

    private Key key;
    private String keyId;

    public boolean hasKey() {
        return keyId != null;
    }

    protected void updateKey(Map.Entry<Key, SignatureKey> keyEntry) {
        key = keyEntry.getKey();
        keyId = keyEntry.getValue().getKeyId();
    }

    public String createJwt(PasswordResetRequest passwordResetRequest, Instant expiryInstant) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setId(passwordResetRequest.getId().toString())
                .setSubject(passwordResetRequest.getPortalUser().getId().toString())
                .setIssuer("doorbell")
//                .setAudience("all")
                .setIssuedAt(Date.from(now))
                .setNotBefore(Date.from(now))
                .setExpiration(Date.from(expiryInstant))
                .signWith(key)
                .compact();
    }

}
