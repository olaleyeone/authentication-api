package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest extends EntityTest {

    private PasswordResetRequest passwordResetRequest;

    @BeforeEach
    void setUp() {
        passwordResetRequest = new PasswordResetRequest();
        passwordResetRequest.setPortalUserIdentifier(modelFactory.create(PortalUserIdentifier.class));
        passwordResetRequest.setExpiresAt(OffsetDateTime.now().plusSeconds(faker.number().randomDigit()));
        String digit = faker.number().digit();
        passwordResetRequest.setResetCode(digit);
        passwordResetRequest.setResetCodeHash(Base64.getEncoder().encodeToString(digit.getBytes()));
        passwordResetRequest.setIpAddress(faker.internet().ipV4Address());
        passwordResetRequest.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void prePersistWithoutCreatedOn() {
        OffsetDateTime now = OffsetDateTime.now();
        passwordResetRequest.setCreatedAt(now);
        saveAndFlush(passwordResetRequest);
        assertNotNull(passwordResetRequest);
        assertEquals(now, passwordResetRequest.getCreatedAt());
    }

    @Test
    void prePersistWithCreatedOn() {
        saveAndFlush(passwordResetRequest);
        assertNotNull(passwordResetRequest);
        assertNotNull(passwordResetRequest.getCreatedAt());
    }

    @Test
    void prePersistWithoutIdentifier() {
        passwordResetRequest.setPortalUserIdentifier(null);
        assertThrows(PersistenceException.class, ()->saveAndFlush(passwordResetRequest));
    }

    @Test
    public void getExpiryInstant() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        passwordResetRequest.setExpiresAt(expiresAt);
        assertEquals(expiresAt.toInstant(), passwordResetRequest.getExpiryInstant());
    }

    @Test
    public void getSecondsTillExpiry() {
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(20);
        passwordResetRequest.setExpiresAt(expiresAt);
        long secondsTillExpiry = Instant.now().until(expiresAt.toInstant(), ChronoUnit.SECONDS);
        assertTrue((secondsTillExpiry - passwordResetRequest.getSecondsTillExpiry()) <= 1);
    }
}