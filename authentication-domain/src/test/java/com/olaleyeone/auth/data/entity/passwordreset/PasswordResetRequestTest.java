package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.entitytest.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest extends EntityTest {

    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPortalUserIdentifier(modelFactory.create(PortalUserIdentifier.class));
        passwordResetRequest.setExpiresOn(LocalDateTime.now().plusSeconds(faker.number().randomDigit()));
        String digit = faker.number().digit();
        passwordResetRequest.setResetCode(digit);
        passwordResetRequest.setResetCodeHash(Base64.getEncoder().encodeToString(digit.getBytes()));
    }

    @Test
    void prePersistWithoutCreatedOn() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetRequest.setCreatedOn(now);
        saveAndFlush(passwordResetRequest);
        assertNotNull(passwordResetRequest);
        assertEquals(now, passwordResetRequest.getCreatedOn());
    }

    @Test
    void prePersistWithCreatedOn() {
        saveAndFlush(passwordResetRequest);
        assertNotNull(passwordResetRequest);
        assertNotNull(passwordResetRequest.getCreatedOn());
    }

    @Test
    void prePersistWithoutIdentifier() {
        passwordResetRequest.setPortalUserIdentifier(null);
        assertThrows(PersistenceException.class, ()->saveAndFlush(passwordResetRequest));
    }

    @Test
    public void getExpiryInstant() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        passwordResetRequest.setExpiresOn(expiresAt);
        assertEquals(expiresAt.atZone(ZoneId.systemDefault()).toInstant(), passwordResetRequest.getExpiryInstant());
    }

    @Test
    public void getSecondsTillExpiry() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(20);
        passwordResetRequest.setExpiresOn(expiresAt);
        long secondsTillExpiry = Instant.now().until(expiresAt.atZone(ZoneId.systemDefault()).toInstant(), ChronoUnit.SECONDS);
        assertTrue((secondsTillExpiry - passwordResetRequest.getSecondsTillExpiry()) <= 1);
    }
}