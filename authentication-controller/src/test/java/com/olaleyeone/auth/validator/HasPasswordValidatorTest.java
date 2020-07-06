package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.repository.PortalUserRepository;
import com.olaleyeone.auth.test.ComponentTest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.inject.Provider;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HasPasswordValidatorTest extends ComponentTest {

    @Mock
    private RequestMetadata requestMetadata;
    @Mock
    private PortalUserRepository portalUserRepository;

    private PortalUser portalUser;
    private HasPasswordValidator validator;

    @BeforeEach
    void setUp() {
        portalUser = new PortalUser();

        Mockito.doReturn(faker.number().randomNumber()).when(requestMetadata).getPortalUserId();
        Mockito.doReturn(Optional.of(portalUser)).when(portalUserRepository).findById(Mockito.any());
        validator = HasPasswordValidator.builder()
                .requestMetadataProvider(() -> requestMetadata)
                .portalUserRepository(portalUserRepository)
                .build();
    }

    @Test
    void isValidWithBlank() {
        portalUser.setPasswordUpdateRequired(false);
        assertFalse(validator.isValid("", null));
    }

    @Test
    void isValid() {
        portalUser.setPasswordUpdateRequired(false);
        assertTrue(validator.isValid(faker.internet().password(), null));
    }

    @Test
    void isValidWithPasswordUpdateRequired() {
        portalUser.setPasswordUpdateRequired(true);
        assertTrue(validator.isValid("", null));
    }
}