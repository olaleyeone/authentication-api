package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.dto.UserRegistrationApiRequest;
import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.AuthenticationType;
import com.olaleyeone.auth.servicetest.ServiceTest;
import com.olaleyeone.data.dto.RequestMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ImplicitAuthenticationServiceImplTest extends ServiceTest {

    @Autowired
    private ImplicitAuthenticationService authenticationService;

    @Autowired
    private RequestMetadata requestMetadata;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(faker.internet().ipV4Address()).when(requestMetadata).getIpAddress();
        Mockito.doReturn(faker.internet().userAgentAny()).when(requestMetadata).getUserAgent();
    }

    @Test
    void createSignUpAuthentication() {
        PortalUser portalUser = modelFactory.create(PortalUser.class);
        UserRegistrationApiRequest apiRequest = new UserRegistrationApiRequest();
        apiRequest.setFirebaseToken(faker.code().asin());
        PortalUserAuthentication signUpAuthentication = authenticationService.createSignUpAuthentication(portalUser, apiRequest);
        assertNotNull(signUpAuthentication);
        assertNotNull(signUpAuthentication.getId());
        assertEquals(apiRequest.getFirebaseToken(), signUpAuthentication.getFirebaseToken());
        assertEquals(portalUser, signUpAuthentication.getPortalUser());
        assertEquals(AuthenticationType.SIGN_UP, signUpAuthentication.getType());
    }

    @Test
    void createPasswordResetAuthentication() {
        PasswordResetRequest passwordResetRequest = modelFactory.create(PasswordResetRequest.class);
        PortalUserAuthentication passwordResetAuthentication = authenticationService.createPasswordResetAuthentication(passwordResetRequest);

        PortalUserIdentifier portalUserIdentifier = passwordResetRequest.getPortalUserIdentifier();

        assertNotNull(passwordResetAuthentication);
        assertNotNull(passwordResetAuthentication.getId());
        assertEquals(portalUserIdentifier.getPortalUser(), passwordResetAuthentication.getPortalUser());
        assertEquals(portalUserIdentifier, passwordResetAuthentication.getPortalUserIdentifier());
        assertEquals(AuthenticationType.PASSWORD_RESET, passwordResetAuthentication.getType());
    }
}