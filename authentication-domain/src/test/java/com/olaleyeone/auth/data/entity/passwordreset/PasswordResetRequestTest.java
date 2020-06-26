package com.olaleyeone.auth.data.entity.passwordreset;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.entitytest.EntityTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}