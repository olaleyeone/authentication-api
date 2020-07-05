package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PortalUserIdentifierVerificationTest extends EntityTest {

    private PortalUserIdentifierVerification portalUserIdentifierVerification;

    @BeforeEach
    void setUp() {
        portalUserIdentifierVerification = new PortalUserIdentifierVerification();
        portalUserIdentifierVerification.setIdentifier(faker.internet().emailAddress());
        portalUserIdentifierVerification.setIdentifierType(UserIdentifierType.EMAIL);
        portalUserIdentifierVerification.setExpiresOn(LocalDateTime.now().plusSeconds(faker.number().randomDigit()));
        String digit = faker.number().digit();
        portalUserIdentifierVerification.setVerificationCode(digit);
        portalUserIdentifierVerification.setVerificationCodeHash(Base64.getEncoder().encodeToString(digit.getBytes()));
    }

    @Test
    void prePersistWithoutCreatedOn() {
        LocalDateTime now = LocalDateTime.now();
        portalUserIdentifierVerification.setCreatedOn(now);
        saveAndFlush(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification);
        assertEquals(now, portalUserIdentifierVerification.getCreatedOn());
    }

    @Test
    void prePersistWithCreatedOn() {
        saveAndFlush(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification);
        assertNotNull(portalUserIdentifierVerification.getCreatedOn());
    }
}