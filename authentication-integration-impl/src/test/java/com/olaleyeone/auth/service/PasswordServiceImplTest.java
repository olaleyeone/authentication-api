package com.olaleyeone.auth.service;

import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceImplTest extends ComponentTest {

    @Spy
    private PasswordServiceImpl passwordService;

    @Test
    void hashPassword() {
        String password = UUID.randomUUID().toString();
        String hash = passwordService.hashPassword(password);
        assertNotNull(hash);
        assertEquals(60, hash.getBytes().length);
    }

    @Test
    void isSameHash() {
        String password = UUID.randomUUID().toString();
        String hash = passwordService.hashPassword(password);
        assertNotNull(hash);
        assertTrue(passwordService.isSameHash(password, hash));
    }
}