package com.olaleyeone.auth.integration.etc;

public interface HashService {

    boolean isSameHash(String rawText, String hash);

    String generateHash(String rawText);
}
