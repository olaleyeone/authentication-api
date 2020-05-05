package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PortalUserIdentifierVerificationServiceImplTest extends ServiceTest {

    @Autowired
    private PortalUserIdentifierVerificationService portalUserIdentifierVerificationService;

    @Autowired
    private PhoneNumberService phoneNumberService;

    @Autowired
    private HashService hashService;

    private String hash;

    @BeforeEach
    public void setUp() {
        hash = faker.random().hex();
        Mockito.doReturn(hash).when(hashService).generateHash(Mockito.anyString());
    }

    @Test
    void createVerificationForEmail() {
        Map.Entry<PortalUserIdentifierVerification, String> verification =
                portalUserIdentifierVerificationService.createVerification(faker.internet().emailAddress(), UserIdentifierType.EMAIL);
        assertNotNull(verification.getKey());
        assertNotNull(verification.getKey().getId());
        assertEquals(hash, verification.getKey().getVerificationCodeHash());
        Mockito.verify(hashService, Mockito.times(1)).generateHash(verification.getValue());
        Mockito.verify(phoneNumberService, Mockito.never()).formatPhoneNumber(Mockito.anyString());
    }

    @Test
    void createVerificationForPhoneNumber() {
        String formattedPhoneNumber = faker.phoneNumber().cellPhone();
        Mockito.doReturn(formattedPhoneNumber).when(phoneNumberService).formatPhoneNumber(Mockito.anyString());

        String rawPhoneNumber = faker.phoneNumber().cellPhone();
        Map.Entry<PortalUserIdentifierVerification, String> verification =
                portalUserIdentifierVerificationService.createVerification(rawPhoneNumber, UserIdentifierType.PHONE_NUMBER);
        assertNotNull(verification);
        assertNotNull(verification.getKey().getId());
        Mockito.verify(phoneNumberService, Mockito.times(1)).formatPhoneNumber(rawPhoneNumber);
        assertEquals(formattedPhoneNumber, verification.getKey().getIdentifier());
    }

    @Test
    void confirmVerificationWithValidCode() {
        String emailAddress = faker.internet().emailAddress();
        Map.Entry<PortalUserIdentifierVerification, String> verification =
                portalUserIdentifierVerificationService.createVerification(emailAddress, UserIdentifierType.EMAIL);
        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.anyString(), Mockito.anyString());
        boolean confirmationResult = portalUserIdentifierVerificationService.confirmVerification(verification.getKey(), verification.getValue());
        assertTrue(confirmationResult);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(verification.getValue(), verification.getKey().getVerificationCodeHash());
    }

    @Test
    void confirmVerificationWithInvalidCode() {
        String emailAddress = faker.internet().emailAddress();
        Map.Entry<PortalUserIdentifierVerification, String> verification =
                portalUserIdentifierVerificationService.createVerification(emailAddress, UserIdentifierType.EMAIL);
        Mockito.doReturn(false).when(hashService).isSameHash(Mockito.anyString(), Mockito.anyString());
        boolean confirmationResult = portalUserIdentifierVerificationService.confirmVerification(verification.getKey(), verification.getValue());
        assertFalse(confirmationResult);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(verification.getValue(), verification.getKey().getVerificationCodeHash());
    }
}