package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.RefreshToken;
import com.olaleyeone.auth.dto.JwtDto;
import com.olaleyeone.auth.security.data.JsonWebToken;

public interface JwtService {

    JwtDto generateJwt(RefreshToken refreshToken);

    JsonWebToken parseAccessToken(String token);
}
