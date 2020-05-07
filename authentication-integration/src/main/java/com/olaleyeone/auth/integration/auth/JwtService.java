package com.olaleyeone.auth.integration.auth;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.security.data.AccessClaims;

public interface JwtService {

    JwtDto generateJwt(RefreshToken refreshToken);

    AccessClaims parseToken(String token);
}
