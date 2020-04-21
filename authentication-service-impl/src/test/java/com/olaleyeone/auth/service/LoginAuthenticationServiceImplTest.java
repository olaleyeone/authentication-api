package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginApiRequest;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginAuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private LoginAuthenticationService authenticationService;

    @Autowired
    private PasswordService passwordService;

    private LoginApiRequest loginApiRequest;
    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        loginApiRequest = new LoginApiRequest();
        loginApiRequest.setIdentifier(faker.internet().emailAddress());
        loginApiRequest.setPassword(faker.internet().password());
        requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(faker.internet().ipV4Address());
        requestMetadata.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void authenticationShouldFailForUnknownUser() {
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertNotNull(userAuthentication.getDateCreated());
        assertEquals(AuthenticationResponseType.UNKNOWN_ACCOUNT, userAuthentication.getResponseType());
        assertEquals(loginApiRequest.getIdentifier(), userAuthentication.getIdentifier());
    }

    @Test
    void authenticationShouldFailForWrongCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        PortalUserIdentifier userIdentifier = getUserIdentifier();
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_CREDENTIAL, userAuthentication.getResponseType());
        assertEquals(userIdentifier.getId(), userAuthentication.getPortalUserIdentifier().getId());
    }

    @Test
    void authenticationShouldSucceedForCorrectCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        PortalUserIdentifier userIdentifier = getUserIdentifier();
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.SUCCESSFUL, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
    }

    private PortalUserIdentifier getUserIdentifier() {
        return modelFactory.pipe(PortalUserIdentifier.class)
                .then(it -> {
                    it.setIdentifier(loginApiRequest.getIdentifier());
                    return it;
                })
                .create();
    }
}