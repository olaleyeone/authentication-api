package com.olaleyeone.auth.integration.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.inject.Named;

@Named
public class HashServiceImpl implements HashService {

    @Override
    public boolean isSameHash(String rawText, String hash) {
        return BCrypt.verifyer().verify(rawText.toCharArray(), hash).verified;
    }

    @Override
    public String generateHash(String rawText) {
        return BCrypt.withDefaults().hashToString(12, rawText.toCharArray());
    }
}
