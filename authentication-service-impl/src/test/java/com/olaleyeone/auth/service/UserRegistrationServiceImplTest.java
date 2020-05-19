package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.UserDataApiRequest;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.data.dto.RequestMetadata;
import com.olaleyeone.auth.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationServiceImplTest extends ServiceTest {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private PhoneNumberService phoneNumberService;
    @Autowired
    private HashService hashService;

    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    private UserRegistrationApiRequest dto;
    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationApiRequest();
        dto.setFirstName(faker.name().firstName());
        dto.setPhoneNumber(faker.phoneNumber().phoneNumber());
        dto.setEmail(faker.internet().emailAddress());
        dto.setPassword(faker.internet().password());

        requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(faker.internet().ipV4Address());
        requestMetadata.setUserAgent(faker.internet().userAgentAny());

        Mockito.doReturn(dto.getPhoneNumber())
                .when(phoneNumberService).formatPhoneNumber(Mockito.anyString());
    }

    @Test
    public void registerUser() {
        dto.setData(Collections.singletonList(UserDataApiRequest.builder()
                .name(faker.name().name())
                .value(faker.lordOfTheRings().character())
                .build()));
        PortalUserAuthentication userAuthentication = userRegistrationService.registerUser(dto, requestMetadata);
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationType.USER_REGISTRATION, userAuthentication.getType());

        assertNotNull(userAuthentication.getPortalUser());
        assertNotNull(userAuthentication.getPortalUser().getId());

        PortalUser portalUser = userAuthentication.getPortalUser();
        assertEquals(dto.getFirstName(), portalUser.getFirstName());
        assertEquals(dto.getLastName(), portalUser.getLastName());
        assertEquals(dto.getOtherName(), portalUser.getOtherName());
        assertNotNull(portalUser.getId());
        assertNotNull(portalUser.getCreatedOn());
    }

    @Test
    public void shouldIgnoreBlankPassword() {
        dto.setPassword("");
        PortalUserAuthentication userAuthentication = userRegistrationService.registerUser(dto, requestMetadata);
        PortalUser portalUser = userAuthentication.getPortalUser();
        assertNull(portalUser.getPassword());
        Mockito.verify(hashService, Mockito.never())
                .generateHash(dto.getPassword());
    }

    @Test
    public void shouldEncryptPassword() {
        String encryptPassword = UUID.randomUUID().toString();
        Mockito.doReturn(encryptPassword)
                .when(hashService).generateHash(Mockito.anyString());
        PortalUserAuthentication userAuthentication = userRegistrationService.registerUser(dto, requestMetadata);
        PortalUser portalUser = userAuthentication.getPortalUser();
        assertEquals(encryptPassword, portalUser.getPassword());
        Mockito.verify(hashService, Mockito.times(1))
                .generateHash(dto.getPassword());
    }

    @Test
    public void shouldSavePhoneNumber() {
        String phoneNumber = UUID.randomUUID().toString();
        Mockito.doReturn(phoneNumber)
                .when(phoneNumberService).formatPhoneNumber(Mockito.anyString());
        PortalUserAuthentication userAuthentication = userRegistrationService.registerUser(dto, requestMetadata);
        PortalUser portalUser = userAuthentication.getPortalUser();

        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(phoneNumber);
        assertTrue(optionalPortalUserIdentifier.isPresent());

        PortalUserIdentifier portalUserIdentifier = optionalPortalUserIdentifier.get();
        assertEquals(portalUser.getId(), portalUserIdentifier.getPortalUser().getId());
        assertEquals(UserIdentifierType.PHONE_NUMBER, portalUserIdentifier.getIdentifierType());

        Mockito.verify(phoneNumberService, Mockito.times(1))
                .formatPhoneNumber(dto.getPhoneNumber());
    }

    @Test
    public void shouldSaveEmail() {
        PortalUserAuthentication userAuthentication = userRegistrationService.registerUser(dto, requestMetadata);
        PortalUser portalUser = userAuthentication.getPortalUser();

        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(dto.getEmail());
        assertTrue(optionalPortalUserIdentifier.isPresent());

        PortalUserIdentifier portalUserIdentifier = optionalPortalUserIdentifier.get();
        assertEquals(portalUser.getId(), portalUserIdentifier.getPortalUser().getId());
        assertEquals(UserIdentifierType.EMAIL, portalUserIdentifier.getIdentifierType());
    }

    @Test
    public void shouldSaveEmailWithValidVerificationCode() {
        dto.setEmailVerificationCode(faker.code().asin());
        PortalUserIdentifierVerification verification = modelFactory.pipe(PortalUserIdentifierVerification.class)
                .then(it -> {
                    it.setIdentifier(dto.getEmail());
                    return it;
                })
                .create();
        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.any(), Mockito.any());

        userRegistrationService.registerUser(dto, requestMetadata);

        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(dto.getEmail());
        assertTrue(optionalPortalUserIdentifier.isPresent());
        PortalUserIdentifier portalUserIdentifier = optionalPortalUserIdentifier.get();
        assertEquals(portalUserIdentifier.getVerification(), verification);

        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(dto.getEmailVerificationCode(), verification.getVerificationCodeHash());
    }

    @Test
    public void shouldSaveEmailWithDuplicateVerificationCode() {
        dto.setEmailVerificationCode(faker.code().asin());
        List<PortalUserIdentifierVerification> verifications = modelFactory.pipe(PortalUserIdentifierVerification.class)
                .then(it -> {
                    it.setIdentifier(dto.getEmail());
                    return it;
                })
                .create(3);
        Mockito.doReturn(true).when(hashService).isSameHash(Mockito.any(), Mockito.any());

        userRegistrationService.registerUser(dto, requestMetadata);

        Iterator<PortalUserIdentifierVerification> iterator = verifications.iterator();
        assertNotNull(iterator.next().getUsedOn());
        while (iterator.hasNext()) {
            assertNotNull(iterator.next().getDeactivatedOn());
        }
    }

    @Test
    public void shouldSaveEmailWithInvalidVerificationCode() {
        dto.setEmailVerificationCode(faker.code().asin());

        assertThrows(IllegalArgumentException.class, () -> userRegistrationService.registerUser(dto, requestMetadata));

        Mockito.verify(hashService, Mockito.never())
                .isSameHash(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldSaveEmailWithWrongHash() {
        dto.setEmailVerificationCode(faker.code().asin());

        PortalUserIdentifierVerification verification = modelFactory.pipe(PortalUserIdentifierVerification.class)
                .then(it -> {
                    it.setIdentifier(dto.getEmail());
                    return it;
                })
                .create();
        Mockito.doReturn(false).when(hashService).isSameHash(Mockito.any(), Mockito.any());

        assertThrows(IllegalArgumentException.class, () -> userRegistrationService.registerUser(dto, requestMetadata));

        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(dto.getEmailVerificationCode(), verification.getVerificationCodeHash());
    }

    @Test
    public void shouldRequireIdentifier() {
        dto.setEmail("");
        dto.setPhoneNumber("");
        assertThrows(IllegalArgumentException.class, () -> userRegistrationService.registerUser(dto, requestMetadata));
    }
}