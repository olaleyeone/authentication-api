package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.SignatureKey;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Key;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class KeyGeneratorImplTest extends ServiceTest {

    @Autowired
    private KeyGeneratorImpl keyGenerator;

    @Test
    void generateKey() {
        Map.Entry<Key, SignatureKey> keyEntry = keyGenerator.generateKey();
        assertNotNull(keyEntry);
        assertNotNull(keyEntry.getValue().getId());
        assertEquals(keyEntry.getKey().getAlgorithm(), keyEntry.getValue().getAlgorithm());
        assertNotEquals(keyEntry.getKey().getEncoded().length, keyEntry.getValue().getEncodedKey().length);
    }
}