package com.olaleyeone.auth.service;

import com.olaleyeone.auth.data.entity.PortalUser;
import com.olaleyeone.auth.data.entity.PortalUserData;
import com.olaleyeone.auth.data.dto.UserDataApiRequest;
import com.olaleyeone.auth.servicetest.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class PortalUserDataServiceImplTest extends ServiceTest {

    @Autowired
    private PortalUserDataService portalUserDataService;

    private PortalUser portalUser;

    @BeforeEach
    void setUp() {
        portalUser = modelFactory.create(PortalUser.class);
    }

    @Test
    void addData() {
        UserDataApiRequest entry = UserDataApiRequest.builder()
                .name(faker.name().name())
                .value(faker.lordOfTheRings().character())
                .build();
        PortalUserData portalUserData = portalUserDataService.addData(portalUser, entry);
        assertNotNull(portalUserData);
        assertNotNull(portalUserData.getId());
        assertEquals(entry.getName(), portalUserData.getName());
        assertEquals(entry.getValue(), portalUserData.getValue());
    }
}