package com.olaleyeone.auth.security.access;

import com.olaleyeone.auth.security.data.JsonWebToken;

public interface AccessTokenValidator {

    JsonWebToken parseToken(String token);
}
