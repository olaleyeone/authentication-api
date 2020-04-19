package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.test.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest extends EntityTest {

    @Test
    public void saveToken() {
        RefreshToken refreshToken = new RefreshToken();
        AuthenticationResponse actualAuthentication = modelFactory.create(AuthenticationResponse.class);
        refreshToken.setActualAuthentication(actualAuthentication);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        saveAndFlush(refreshToken);
        entityManager.refresh(refreshToken);
        assertNotNull(refreshToken.getDateCreated());
        assertEquals(expiresAt, refreshToken.getExpiresAt());
        assertEquals(actualAuthentication.getId(), refreshToken.getActualAuthentication().getId());
    }

    @Test
    public void shouldNotSaveWithoutExpiryTime() {
        RefreshToken refreshToken = new RefreshToken();
        AuthenticationResponse actualAuthentication = modelFactory.create(AuthenticationResponse.class);
        refreshToken.setActualAuthentication(actualAuthentication);
        assertThrows(PersistenceException.class, () -> saveAndFlush(refreshToken));
    }

    @Test
    public void shouldSaveWithoutActualAuthentication() {
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        saveAndFlush(refreshToken);
        entityManager.refresh(refreshToken);
        assertNotNull(refreshToken.getId());
    }
}