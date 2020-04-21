package com.olaleyeone.auth.security.data;

public interface AuthorizedRequest {

    String getIpAddress();

    String getUserAgent();

    String getAccessToken();

    AccessClaims getAccessClaims();

    boolean isLocalhost();
}
