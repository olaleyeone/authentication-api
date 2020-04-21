package com.olaleyeone.auth.security.data;

public interface AccessClaimsExtractor {

    AccessClaims getClaims(String token);
}
