package com.olaleyeone.auth.security.data;

import java.time.Instant;
import java.util.List;

public interface JsonWebToken {

//    jti, iss, sub, aud, iat, nbf, exp

    String getId();

    String getIssuer();

    String getSubject();

    List<String> getAudience();

    Instant getExpirationTime();

    Instant getStartTime();

    Instant getTimeIssued();
}
