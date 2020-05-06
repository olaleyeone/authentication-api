package com.olaleyeone.auth.validator;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import com.olaleyeone.auth.test.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ValidEmailRegistrationCodeValidatorTest extends ComponentTest {

    @Mock
    private PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    @Mock
    private HashService hashService;

    @InjectMocks
    private ValidEmailRegistrationCodeValidator validator;

    private UserRegistrationApiRequest userRegistrationApiRequest;

    @BeforeEach
    public void setUp() {
        userRegistrationApiRequest = new UserRegistrationApiRequest();
    }

    @Test
    void isValid_BlankCode() {
        assertTrue(validator.isValid(userRegistrationApiRequest, null));
        Mockito.verify(portalUserIdentifierVerificationRepository, Mockito.never()).getAllActive(Mockito.any());
        Mockito.verify(hashService, Mockito.never()).isSameHash(Mockito.any(), Mockito.any());
    }

    @Test
    void isValid_NoCode() {
        String emailAddress = faker.internet().emailAddress();
        userRegistrationApiRequest.setEmail(emailAddress);
        userRegistrationApiRequest.setEmailVerificationCode(faker.code().asin());
        assertFalse(validator.isValid(userRegistrationApiRequest, null));
        Mockito.verify(portalUserIdentifierVerificationRepository, Mockito.times(1)).getAllActive(emailAddress);
        Mockito.verify(hashService, Mockito.never()).isSameHash(Mockito.any(), Mockito.any());
    }

    @Test
    void isValid_InvalidCode() {
        String emailAddress = faker.internet().emailAddress();
        userRegistrationApiRequest.setEmail(emailAddress);
        userRegistrationApiRequest.setEmailVerificationCode(faker.code().asin());

        PortalUserIdentifierVerification verification = new PortalUserIdentifierVerification();
        verification.setVerificationCodeHash(faker.code().asin());
        Mockito.doReturn(Collections.singletonList(verification)).when(portalUserIdentifierVerificationRepository).getAllActive(Mockito.any());
        Mockito.doReturn(false).when(hashService).isSameHash(Mockito.any(), Mockito.any());
        assertFalse(validator.isValid(userRegistrationApiRequest, null));
        Mockito.verify(portalUserIdentifierVerificationRepository, Mockito.times(1)).getAllActive(emailAddress);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(userRegistrationApiRequest.getEmailVerificationCode(), verification.getVerificationCodeHash());
    }

    @Test
    void isValid_ValidCode() {
        String emailAddress = faker.internet().emailAddress();
        userRegistrationApiRequest.setEmail(emailAddress);
        userRegistrationApiRequest.setEmailVerificationCode(faker.code().asin());

        PortalUserIdentifierVerification verification = new PortalUserIdentifierVerification();
        verification.setVerificationCodeHash(faker.code().asin());
        Mockito.doReturn(Collections.singletonList(verification)).when(portalUserIdentifierVerificationRepository).getAllActive(Mockito.any());
        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.any(), Mockito.any());
        assertTrue(validator.isValid(userRegistrationApiRequest, null));
        Mockito.verify(portalUserIdentifierVerificationRepository, Mockito.times(1)).getAllActive(emailAddress);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(userRegistrationApiRequest.getEmailVerificationCode(), verification.getVerificationCodeHash());
    }
}