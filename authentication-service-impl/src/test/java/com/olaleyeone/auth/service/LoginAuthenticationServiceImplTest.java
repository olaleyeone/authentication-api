package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.LoginApiRequest;
import com.olaleyeone.auth.data.dto.UserDataApiRequest;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginAuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private LoginAuthenticationService authenticationService;

    @Autowired
    private PortalUserAuthenticationDataRepository portalUserAuthenticationDataRepository;

    @Autowired
    private HashService hashService;

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
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        PortalUserIdentifier userIdentifier = createUserIdentifier();
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.INCORRECT_CREDENTIAL, userAuthentication.getResponseType());
        assertEquals(userIdentifier.getId(), userAuthentication.getPortalUserIdentifier().getId());
    }

    @Test
    void authenticationShouldSucceedForCorrectCredential() {
        Mockito.when(hashService.isSameHash(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        PortalUserIdentifier userIdentifier = createUserIdentifier();
        PortalUserAuthentication userAuthentication = authenticationService.getAuthenticationResponse(loginApiRequest, requestMetadata);
        Mockito.verify(hashService, Mockito.times(1))
                .isSameHash(loginApiRequest.getPassword(), userIdentifier.getPortalUser().getPassword());
        assertNotNull(userAuthentication);
        assertNotNull(userAuthentication.getId());
        assertEquals(AuthenticationResponseType.SUCCESSFUL, userAuthentication.getResponseType());
        assertEquals(userIdentifier, userAuthentication.getPortalUserIdentifier());
        assertEquals(userIdentifier.getPortalUser(), userAuthentication.getPortalUser());
    }

    @Test
    void deactivateOtherSessions() {
        PortalUserIdentifier userIdentifier = createUserIdentifier();
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
        createUserIdentifier();
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

    private PortalUserIdentifier createUserIdentifier() {
        return modelFactory.pipe(PortalUserIdentifier.class)
                .then(it -> {
                    it.setIdentifier(loginApiRequest.getIdentifier());
                    return it;
                })
                .create();
    }
}