package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.etc.HashService;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.data.RequestMetadata;
import com.olaleyeone.auth.dto.data.UserRegistrationApiRequest;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

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

        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findByIdentifier(phoneNumber);
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

        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findByIdentifier(dto.getEmail());
        assertTrue(optionalPortalUserIdentifier.isPresent());

        PortalUserIdentifier portalUserIdentifier = optionalPortalUserIdentifier.get();
        assertEquals(portalUser.getId(), portalUserIdentifier.getPortalUser().getId());
        assertEquals(UserIdentifierType.EMAIL, portalUserIdentifier.getIdentifierType());
    }

    @Test
    public void shouldRequireIdentifier() {
        dto.setEmail("");
        dto.setPhoneNumber("");
        assertThrows(IllegalArgumentException.class, () -> userRegistrationService.registerUser(dto, requestMetadata));
    }
}