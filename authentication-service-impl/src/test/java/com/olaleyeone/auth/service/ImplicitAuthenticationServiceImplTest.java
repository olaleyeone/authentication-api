package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.dto.data.RequestMetadata;
import com.olaleyeone.auth.test.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImplicitAuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private ImplicitAuthenticationService authenticationService;

    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        requestMetadata = new RequestMetadata();
        requestMetadata.setIpAddress(faker.internet().ipV4Address());
        requestMetadata.setUserAgent(faker.internet().userAgentAny());
    }

    @Test
    void createSignUpAuthentication() {
        PortalUser portalUser = modelFactory.create(PortalUser.class);
        PortalUserAuthentication signUpAuthentication = authenticationService.createSignUpAuthentication(portalUser, requestMetadata);
        assertNotNull(signUpAuthentication);
        assertNotNull(signUpAuthentication.getId());
        assertEquals(portalUser, signUpAuthentication.getPortalUser());
        assertEquals(AuthenticationType.USER_REGISTRATION, signUpAuthentication.getType());
    }
}