package com.olaleyeone.auth.integration.security;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AuthorizedRequest;
import lombok.Data;

import java.time.Instant;

@Data
class SimpleAuthorizedRequest implements AuthorizedRequest {

    private String ipAddress;
    private String userAgent;
    private String accessToken;
    private boolean localhost;
    private AccessClaims accessClaims;

    @Override
    public boolean isAccessTokenExpired() {
        if (accessClaims.getExpirationTime() == null) {
            return false;
        }
        return accessClaims.getExpirationTime().isBefore(Instant.now());
    }
}
