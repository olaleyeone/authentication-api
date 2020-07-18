package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.RefreshToken;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest extends EntityTest {

    @Test
    public void saveToken() {
        RefreshToken refreshToken = new RefreshToken();
        PortalUserAuthentication userAuthentication = modelFactory.create(PortalUserAuthentication.class);
        refreshToken.setActualAuthentication(userAuthentication);
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        OffsetDateTime accessExpiresAt = OffsetDateTime.now().plusMinutes(2);
        refreshToken.setAccessExpiresAt(accessExpiresAt);
        saveAndFlush(refreshToken);
        entityManager.refresh(refreshToken);
        assertNotNull(refreshToken.getCreatedOn());
        assertNotNull(refreshToken.getPortalUser());
        assertEquals(expiresAt, refreshToken.getExpiresAt());
        assertEquals(accessExpiresAt, refreshToken.getAccessExpiresAt());
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
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        assertThrows(PersistenceException.class, () -> saveAndFlush(refreshToken));
    }

    @Test
    public void getExpiryInstant() {
        RefreshToken refreshToken = new RefreshToken();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        assertEquals(expiresAt.toInstant(), refreshToken.getExpiryInstant());
    }

    @Test
    public void getNullExpiryInstant() {
        RefreshToken refreshToken = new RefreshToken();
        assertNull(refreshToken.getExpiryInstant());
        assertNull(refreshToken.getSecondsTillExpiry());
    }

    @Test
    public void getSecondsTillExpiry() {
        RefreshToken refreshToken = new RefreshToken();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setExpiresAt(expiresAt);
        long secondsTillExpiry = Instant.now().until(expiresAt.toInstant(), ChronoUnit.SECONDS);
        assertTrue((secondsTillExpiry - refreshToken.getSecondsTillExpiry()) <= 1);
    }

    @Test
    public void getAccessExpiryInstant() {
        RefreshToken refreshToken = new RefreshToken();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setAccessExpiresAt(expiresAt);
        assertEquals(expiresAt.toInstant(), refreshToken.getAccessExpiryInstant());
    }

    @Test
    public void getAccessSecondsTillExpiry() {
        RefreshToken refreshToken = new RefreshToken();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        refreshToken.setAccessExpiresAt(expiresAt);
        long secondsTillExpiry = Instant.now().until(expiresAt.toInstant(), ChronoUnit.SECONDS);
        assertTrue((secondsTillExpiry - refreshToken.getSecondsTillAccessExpiry()) <= 1);
    }
}