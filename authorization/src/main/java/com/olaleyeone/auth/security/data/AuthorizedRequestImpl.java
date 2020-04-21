package com.olaleyeone.auth.security.data;

import lombok.Data;

@Data
class AuthorizedRequestImpl implements AuthorizedRequest {

    private String ipAddress;
    private String userAgent;
    private String accessToken;
    private boolean localhost;
    private AccessClaims accessClaims;
}
