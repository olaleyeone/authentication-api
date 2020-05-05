package com.olaleyeone.auth.integration.etc;

public interface HashService {

    boolean isSameHash(String password, String hash);

    String generateHash(String password);
}
