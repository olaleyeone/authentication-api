package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.test.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

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
        assertNotNull(refreshToken.getPortalUser());
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

    @Test
    public void getExpiryInstant() {
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        assertEquals(expiresAt.atZone(ZoneId.systemDefault()).toInstant(), refreshToken.getExpiryInstant());
    }

    @Test
    public void getSecondsTillExpiry() {
        RefreshToken refreshToken = new RefreshToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        long secondsTillExpiry = Instant.now().until(expiresAt.atZone(ZoneId.systemDefault()).toInstant(), ChronoUnit.SECONDS);
        assertTrue((secondsTillExpiry - refreshToken.getSecondsTillExpiry()) <= 1);
    }
}