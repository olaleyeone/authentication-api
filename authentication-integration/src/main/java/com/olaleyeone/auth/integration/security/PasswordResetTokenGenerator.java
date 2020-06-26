package com.olaleyeone.auth.integration.security;

import com.olaleyeone.auth.data.dto.JwtDto;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;

public interface PasswordResetTokenGenerator {

    JwtDto generateJwt(PasswordResetRequest refreshToken);
}
