package com.olaleyeone.auth.service;

public interface PasswordService {

    boolean isSameHash(String password, String hash);

    String hashPassword(String password);
}
