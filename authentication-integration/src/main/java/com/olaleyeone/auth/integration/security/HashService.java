package com.olaleyeone.auth.integration.security;

public interface HashService {

    boolean isSameHash(String rawText, String hash);

    String generateHash(String rawText);
}
