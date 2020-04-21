package com.olaleyeone.auth.security.data;

public interface RequestMetadata {

    String getIpAddress();

    String getUserAgent();

    String getAccessToken();

    AccessClaims getAccessClaims();

    boolean isLocalhost();
}
