package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.data.dto.JwtDto;

public interface AuthTokenGenerator {

    JwtDto generateJwt(RefreshToken refreshToken);
}
