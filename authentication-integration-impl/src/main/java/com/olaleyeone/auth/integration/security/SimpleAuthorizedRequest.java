package com.olaleyeone.auth.integration.security;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AuthorizedRequest;
import lombok.Data;

@Data
class SimpleAuthorizedRequest implements AuthorizedRequest {

    private String ipAddress;
    private String userAgent;
    private String accessToken;
    private boolean localhost;
    private AccessClaims accessClaims;
}
