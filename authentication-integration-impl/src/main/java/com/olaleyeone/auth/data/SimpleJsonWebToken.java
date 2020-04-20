package com.olaleyeone.auth.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.olaleyeone.auth.security.data.JsonWebToken;
import io.jsonwebtoken.Claims;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SimpleJsonWebToken implements JsonWebToken {

    private final Claims claims;
    private final Gson gson;
    private static final Type type = new TypeToken<List<String>>() {
    }.getType();

    public SimpleJsonWebToken(Claims claims, Gson gson) {
        this.claims = claims;
        this.gson = gson;
    }

    @Override
    public String getId() {
        return claims.getId();
    }

    @Override
    public String getIssuer() {
        return claims.getIssuer();
    }

    @Override
    public String getSubject() {
        return claims.getSubject();
    }

    @Override
    public List<String> getAudience() {

        return Optional.ofNullable(claims.getAudience())
                .map(aud -> (List<String>) gson.fromJson(aud, type))
                .orElse(Collections.EMPTY_LIST);
    }

    @Override
    public Instant getExpirationTime() {
        return Optional.ofNullable(claims.getExpiration())
                .map(date -> Instant.ofEpochMilli(date.getTime()))
                .orElse(null);
    }

    @Override
    public Instant getStartTime() {
        return Optional.ofNullable(claims.getNotBefore())
                .map(date -> Instant.ofEpochMilli(date.getTime()))
                .orElse(null);
    }

    @Override
    public Instant getTimeIssued() {
        return Optional.ofNullable(claims.getIssuedAt())
                .map(date -> Instant.ofEpochMilli(date.getTime()))
                .orElse(null);
    }
}
