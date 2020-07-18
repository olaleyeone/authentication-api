package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogoutServiceImplTest extends ServiceTest {

    @Autowired
    private LogoutService logoutService;

    @Test
    void logout() {
        PortalUserAuthentication userAuthentication = modelFactory.create(PortalUserAuthentication.class);
        assertNull(userAuthentication.getLoggedOutAt());
        logoutService.logout(userAuthentication);
        entityManager.flush();
        entityManager.refresh(userAuthentication);
        assertNotNull(userAuthentication.getLoggedOutAt());
    }
}