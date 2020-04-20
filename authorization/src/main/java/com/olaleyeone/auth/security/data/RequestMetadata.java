package com.olaleyeone.auth.security.data;

public interface RequestMetadata {

    String getIpAddress();

    String getUserAgent();

    String getAccessToken();

    JsonWebToken getTokenClaims();

    boolean isLocalhost();
}
