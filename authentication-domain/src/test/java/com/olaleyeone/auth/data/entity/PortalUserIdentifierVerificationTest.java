package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PortalUserIdentifierVerificationTest extends EntityTest {

    private PortalUserIdentifierVerification portalUserIdentifierVerification;

    @BeforeEach
    void setUp() {
        portalUserIdentifierVerification = new PortalUserIdentifierVerification();
        portalUserIdentifierVerification.setIdentifier(faker.internet().emailAddress());
        portalUserIdentifierVerification.setIdentifierType(UserIdentifierType.EMAIL);
        portalUserIdentifierVerification.setExpiresAt(OffsetDateTime.now().plusSeconds(faker.number().randomDigit()));
        String digit = faker.number().digit();
        portalUserIdentifierVerification.setVerificationCode(digit);
        portalUserIdentifierVerification.setVerificationCodeHash(Base64.getEncoder().encodeToString(digit.getBytes()));
    }

    @Test
    void prePersistWithoutCreatedOn() {
        OffsetDateTime now = OffsetDateTime.now();
        portalUserIdentifierVerification.setCreatedAt(now);
        saveAndFlush(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification);
        assertEquals(now, portalUserIdentifierVerification.getCreatedAt());
    }

    @Test
    void prePersistWithCreatedOn() {
        saveAndFlush(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification.getCreatedAt());
    }
}