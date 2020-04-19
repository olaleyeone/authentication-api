package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.constraints.UniqueIdentifier;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueIdentifierValidatorTest extends ComponentTest {

    @InjectMocks
    private UniqueIdentifierValidator validator;

    @Mock
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Mock
    private PhoneNumberService phoneNumberService;

    @Mock
    private UniqueIdentifier uniqueIdentifier;

    @Test
    void nullShouldBeValid() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void emptyStringShouldBeValid() {
        assertTrue(validator.isValid("", null));
    }

    @Test
    void shouldFormatPhoneNumber() {
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(uniqueIdentifier.value()).thenReturn(UserIdentifierType.PHONE_NUMBER);
        validator.initialize(uniqueIdentifier);

        String value1 = "1234";
        String value2 = "5678";

        Mockito.when(phoneNumberService.formatPhoneNumber(Mockito.anyString())).thenReturn(value2);

        validator.isValid(value1, null);

        Mockito.verify(phoneNumberService, Mockito.times(1))
                .formatPhoneNumber(value1);
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value2);
    }

    @Test
    void shouldNotFormatUsername() {
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());

        Mockito.when(uniqueIdentifier.value()).thenReturn(UserIdentifierType.USERNAME);
        validator.initialize(uniqueIdentifier);

        String value1 = "1234";
        validator.isValid(value1, null);

        Mockito.verify(phoneNumberService, Mockito.never()).formatPhoneNumber(Mockito.anyString());
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }

    @Test
    void shouldBeValid() {
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());

        String value1 = "1234";
        assertTrue(validator.isValid(value1, null));
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }

    @Test
    void shouldBeInValid() {
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.of(new PortalUserIdentifier()));

        String value1 = "1234";
        assertFalse(validator.isValid(value1, null));
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }
}