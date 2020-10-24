package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.dto.UserDataApiRequest;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthenticationData;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.integration.security.HashService;
import com.olaleyeone.auth.repository.PortalUserAuthenticationDataRepository;
import com.olaleyeone.auth.servicetest.ServiceTest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TotpLoginAuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private TotpLoginAuthenticationService authenticationService;

    @Autowired
    private PortalUserAuthenticationDataRepository portalUserAuthenticationDataRepository;

    @Autowired
    private HashService hashService;

    private OneTimePassword oneTimePassword;
    private PortalUserIdentifier userIdentifier;
    private TotpLoginApiRequest loginApiRequest;
    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        oneTimePassword = modelFactory.create(OneTimePassword.class);
        userIdentifier = oneTimePassword.getUserIdentifier();

        loginApiRequest = new TotpLoginApiRequest();
        loginApiRequest.setIdentifier(oneTimePassword.getUserIdentifier().getIdentifier());
        loginApiRequest.setPassword(faker.internet().password());
        loginApiRequest.setTransactionId(oneTimePassword.getId().toString());

        requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(faker.internet().ipV4Address());
        requestMetadata.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void authenticationShouldFailForUnknownUser() {
        loginApiRequest.setIdentifier(UUID.randomUUID().toString());
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertNotNull(userAuthentication.getDateCreated());
        assertEquals(AuthenticationResponseType.UNKNOWN_ACCOUNT, userAuthentication.getResponseType());
        assertEquals(loginApiRequest.getIdentifier(), userAuthentication.getIdentifier());
        assertNull(userAuthentication.getOneTimePassword());
    }

    @Test
    void authenticationShouldFailForWrongCredential() {
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), oneTimePassword.getHash());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_CREDENTIAL, userAuthentication.getResponseType());
        assertEquals(userIdentifier.getId(), userAuthentication.getPortalUserIdentifier().getId());
        assertEquals(oneTimePassword, userAuthentication.getOneTimePassword());
    }

    @Test
    void authenticationShouldSucceedForCorrectCredential() {
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), oneTimePassword.getHash());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.SUCCESSFUL, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
        assertEquals(oneTimePassword, userAuthentication.getOneTimePassword());
    }

    @Test
    void authenticateWithUnknownOtp() {
        loginApiRequest.setTransactionId(String.valueOf(oneTimePassword.getId() + 1));
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.never())
                .isSameHash(loginApiRequest.getPassword(), oneTimePassword.getHash());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.UNKNOWN_OTP, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
        assertNull(userAuthentication.getOneTimePassword());
    }

    @Test
    void authenticateWithIncorrectIdentifier() {
        PortalUserIdentifier userIdentifier = modelFactory.create(PortalUserIdentifier.class);
        loginApiRequest.setIdentifier(userIdentifier.getIdentifier());
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.never())
                .isSameHash(loginApiRequest.getPassword(), oneTimePassword.getHash());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_IDENTIFIER, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
        assertEquals(oneTimePassword, userAuthentication.getOneTimePassword());
    }

    @Test
    void authenticateWithExpiredOtp() {
        oneTimePassword.setExpiresAt(OffsetDateTime.now().minusSeconds(1));
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), oneTimePassword.getHash());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.EXPIRED, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
        assertEquals(oneTimePassword, userAuthentication.getOneTimePassword());
    }

    @Test
    void deactivateOtherSessions() {
        PortalUserAuthentication portalUserAuthentication = modelFactory.pipe(PortalUserAuthentication.class)
                .then(it -> {
                    it.setPortalUserIdentifier(userIdentifier);
                    it.setPortalUser(userIdentifier.getPortalUser());
                    it.setResponseType(AuthenticationResponseType.SUCCESSFUL);
                    return it;
                }).create();

        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        loginApiRequest.setInvalidateOtherSessions(true);
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);

        assertNotNull(userAuthentication);
        assertEquals(AuthenticationResponseType.SUCCESSFUL, userAuthentication.getResponseType());
        assertNull(userAuthentication.getDeactivatedAt());
        entityManager.refresh(portalUserAuthentication);
        assertNotNull(portalUserAuthentication.getDeactivatedAt());
    }

    @Test
    void saveAuthenticationData() {
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        UserDataApiRequest dataApiRequest = UserDataApiRequest.builder()
                .name(faker.book().author())
                .value(faker.book().title())
                .build();
        loginApiRequest.setData(Arrays.asList(dataApiRequest));
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);

        assertNotNull(userAuthentication);
        assertEquals(AuthenticationResponseType.SUCCESSFUL, userAuthentication.getResponseType());

        List<PortalUserAuthenticationData> dataList = portalUserAuthenticationDataRepository.findByPortalUserAuthentication(userAuthentication);
        assertEquals(1, dataList.size());
        PortalUserAuthenticationData data = dataList.iterator().next();
        assertEquals(dataApiRequest.getName(), data.getName());
        assertEquals(dataApiRequest.getValue(), data.getValue());
    }
}