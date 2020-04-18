package com.olaleyeone.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

import javax.inject.Named;

@Named
public class PasswordServiceImpl implements PasswordService {

    @Override
    public boolean isSameHash(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    @Override
    public String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
