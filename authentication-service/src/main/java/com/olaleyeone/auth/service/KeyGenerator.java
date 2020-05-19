package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.data.enums.JwtTokenType;

import java.security.Key;
import java.util.Map;

public interface KeyGenerator {

    Map.Entry<Key, SignatureKey> generateKey(JwtTokenType jwtTokenType);
}
