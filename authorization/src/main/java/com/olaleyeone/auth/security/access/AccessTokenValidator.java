package com.olaleyeone.auth.security.access;

public interface AccessTokenValidator {

    String resolveToUserId(String token);
}
