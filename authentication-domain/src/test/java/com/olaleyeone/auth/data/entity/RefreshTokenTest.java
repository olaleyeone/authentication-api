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
        PortalUserAuthentication userAuthentication = modelFactory.create(PortalUserAuthentication.class);
        refreshToken.setActualAuthentication(userAuthentication);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        saveAndFlush(refreshToken);
        entityManager.refresh(refreshToken);
        assertNotNull(refreshToken.getDateCreated());
        assertEquals(expiresAt, refreshToken.getExpiresAt());
        assertEquals(userAuthentication.getId(), refreshToken.getActualAuthentication().getId());
    }

    @Test
    public void shouldNotSaveWithoutExpiryTime() {
        RefreshToken refreshToken = new RefreshToken();
        PortalUserAuthentication userAuthentication = modelFactory.create(PortalUserAuthentication.class);
        refreshToken.setActualAuthentication(userAuthentication);
        assertThrows(PersistenceException.class, () -> saveAndFlush(refreshToken));
    }

    @Test
    public void shouldNotSaveWithoutActualAuthentication() {
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        assertThrows(PersistenceException.class, () -> saveAndFlush(refreshToken));
    }
}