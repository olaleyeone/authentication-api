package com.olaleyeone.auth.data.entity;

import com.olaleyeone.auth.test.entity.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OneTimePasswordTest extends EntityTest {

    private OneTimePassword oneTimePassword;

    @BeforeEach
    void setUp() {
        oneTimePassword = new OneTimePassword();
        oneTimePassword.setIdentifier(modelFactory.create(PortalUserIdentifier.class));
        oneTimePassword.setExpiresOn(OffsetDateTime.now().plusSeconds(faker.number().randomDigit()));
        String digit = faker.number().digit();
        oneTimePassword.setVerificationCode(digit);
        oneTimePassword.setVerificationCodeHash(Base64.getEncoder().encodeToString(digit.getBytes()));
    }

    @Test
    void prePersistWithoutCreatedOn() {
        OffsetDateTime now = OffsetDateTime.now();
        oneTimePassword.setCreatedOn(now);
        saveAndFlush(oneTimePassword);
        assertNotNull(oneTimePassword);
        assertEquals(now, oneTimePassword.getCreatedOn());
    }

    @Test
    void prePersistWithCreatedOn() {
        saveAndFlush(oneTimePassword);
        assertNotNull(oneTimePassword);
        assertNotNull(oneTimePassword.getCreatedOn());
    }
}