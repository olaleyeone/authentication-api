package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.AuthenticationResponse;
import com.olaleyeone.auth.data.entity.UserIdentifier;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.dto.LoginRequestDto;
import com.olaleyeone.auth.dto.RequestMetadata;
import com.olaleyeone.auth.test.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TestTransaction;

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
        Mockito.reset(passwordService);
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
        assertEquals(AuthenticationResponseType.UNKNOWN_ACCOUNT, authenticationResponse.getAuthenticationResponseType());
        assertEquals(loginRequestDto.getIdentifier(), authenticationResponse.getIdentifier());
    }

    @Test
    void authenticationShouldFailForWrongCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        UserIdentifier userIdentifier = getUserIdentifier();
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(loginRequestDto, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginRequestDto.getPassword(), userIdentifier.getUser().getPassword());
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_CREDENTIAL, authenticationResponse.getAuthenticationResponseType());
        assertEquals(userIdentifier.getId(), authenticationResponse.getUserIdentifier().getId());
    }

    @Test
    void authenticationShouldSucceedForCorrectCredential() {
        Mockito.when(passwordService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        UserIdentifier userIdentifier = getUserIdentifier();
        AuthenticationResponse authenticationResponse = authenticationService.getAuthenticationResponse(loginRequestDto, requestMetadata);
        Mockito.verify(passwordService, Mockito.times(1))
                .isSameHash(loginRequestDto.getPassword(), userIdentifier.getUser().getPassword());
        assertNotNull(authenticationResponse);
        assertNotNull(authenticationResponse.getId());
        assertEquals(AuthenticationResponseType.SUCCESSFUL, authenticationResponse.getAuthenticationResponseType());
        assertEquals(userIdentifier.getId(), authenticationResponse.getUserIdentifier().getId());
    }

    private UserIdentifier getUserIdentifier() {
        return modelFactory.pipe(UserIdentifier.class)
                .then(it -> {
                    it.setIdentifier(loginRequestDto.getIdentifier());
                    return it;
                })
                .create();
    }
}