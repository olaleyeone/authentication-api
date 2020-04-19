package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.constraints.UniqueIdentifier;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PhoneNumberService;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniqueIdentifierValidatorTest extends ComponentTest {

    private UniqueIdentifierValidator validator;

    @MockBean
    private PortalUserIdentifierRepository portalUserIdentifierRepository;
    @MockBean
    private PhoneNumberService phoneNumberService;

    @BeforeEach
    public void setUp() {
        validator = new UniqueIdentifierValidator(portalUserIdentifierRepository, phoneNumberService);
    }

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
        String value1 = "1234";
        String value2 = "5678";
        UniqueIdentifier uniqueIdentifier = Mockito.mock(UniqueIdentifier.class);
        Mockito.when(uniqueIdentifier.value()).thenReturn(UserIdentifierType.PHONE_NUMBER);
        validator.initialize(uniqueIdentifier);
        Mockito.when(phoneNumberService.formatPhoneNumber(Mockito.anyString())).thenReturn(value2);
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());

        assertTrue(validator.isValid(value1, null));

        Mockito.verify(phoneNumberService, Mockito.times(1))
                .formatPhoneNumber(value1);
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value2);
    }

    @Test
    void shouldOnlyFormatPhoneNumber() {
        String value1 = "1234";
        UniqueIdentifier uniqueIdentifier = Mockito.mock(UniqueIdentifier.class);
        Mockito.when(uniqueIdentifier.value()).thenReturn(UserIdentifierType.EMAIL);
        validator.initialize(uniqueIdentifier);
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());

        assertTrue(validator.isValid(value1, null));

        Mockito.verify(phoneNumberService, Mockito.never()).formatPhoneNumber(value1);
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }

    @Test
    void shouldBeValid() {
        String value1 = "1234";
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.empty());
        assertTrue(validator.isValid(value1, null));
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }

    @Test
    void shouldBeInValid() {
        String value1 = "1234";
        Mockito.when(portalUserIdentifierRepository.findByIdentifier(Mockito.anyString())).thenReturn(Optional.of(new PortalUserIdentifier()));
        assertFalse(validator.isValid(value1, null));
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(value1);
    }
}