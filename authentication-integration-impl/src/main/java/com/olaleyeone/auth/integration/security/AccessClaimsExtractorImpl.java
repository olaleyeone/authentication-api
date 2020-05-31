package com.olaleyeone.auth.integration.security;

import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.google.gson.Gson;
import com.olaleyeone.auth.data.SimpleAccessClaims;
import io.jsonwebtoken.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AccessClaimsExtractorImpl implements AccessClaimsExtractor {

    private final SigningKeyResolver signingKeyResolver;
    private final Gson gson;

    @Getter(value = AccessLevel.PRIVATE, lazy = true)
    private final JwtParser jwtParser = createJwtParser();

    private JwtParser createJwtParser() {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(signingKeyResolver)
                .build();
    }

    @Override
    public AccessClaims getClaims(String jws) {
        Jws<Claims> parsedJws = getJwtParser().parseClaimsJws(jws);
//        if (parsedJws.getHeader().getAlgorithm().equals("none")) {
//            return null;
//        }
        Claims claims = parsedJws.getBody();
        return new SimpleAccessClaims(claims, gson);
    }

}
