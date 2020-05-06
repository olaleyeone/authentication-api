package com.olaleyeone.auth.integration.etc;

import com.olaleyeone.auth.integration.etc.HashServiceImpl;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HashServiceImplTest extends ComponentTest {

    @Spy
    private HashServiceImpl passwordService;

    @Test
    void hashPassword() {
        String password = UUID.randomUUID().toString();
        String hash = passwordService.generateHash(password);
        assertNotNull(hash);
        assertEquals(60, hash.getBytes().length);
    }

    @Test
    void isSameHash() {
        String password = UUID.randomUUID().toString();
        String hash = passwordService.generateHash(password);
        assertNotNull(hash);
        assertTrue(passwordService.isSameHash(password, hash));
    }
}