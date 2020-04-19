package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.data.LoginRequestDto;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.test.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PasswordService passwordService;

    private LoginRequestDto loginRequestDto;
    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setIdentifier(faker.internet().emailAddress());
        loginRequestDto.setPassword(faker.internet().password());
        requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(faker.internet().ipV4Address());
        requestMetadata.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void authenticationShouldFailForUnknownUser() {
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(loginRequestDto, requestMetadata);
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getId());
        assertNotNull(authenticationResponse.getDateCreated());
        assertEquals(AuthenticationResponseType.UNKNOWN_ACCOUNT, authenticationResponse.getResponseType());
        assertEquals(loginRequestDto.getIdentifier(), authenticationResponse.getIdentifier());
    }

    @Test
    void authenticationShouldFailForWrongCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        PortalUserIdentifier userIdentifier = getUserIdentifier();
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(loginRequestDto, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginRequestDto.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_CREDENTIAL, authenticationResponse.getResponseType());
        assertEquals(userIdentifier.getId(), authenticationResponse.getPortalUserIdentifier().getId());
    }

    @Test
    void authenticationShouldSucceedForCorrectCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        PortalUserIdentifier userIdentifier = getUserIdentifier();
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(loginRequestDto, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginRequestDto.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getId());
        assertEquals(AuthenticationResponseType.SUCCESSFUL, authenticationResponse.getResponseType());
        assertEquals(userIdentifier.getId(), authenticationResponse.getPortalUserIdentifier().getId());
    }

    private PortalUserIdentifier getUserIdentifier() {
        return modelFactory.pipe(PortalUserIdentifier.class)
                .then(it -> {
                    it.setIdentifier(loginRequestDto.getIdentifier());
                    return it;
                })
                .create();
    }
}