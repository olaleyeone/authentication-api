package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIdentifierControllerTest extends ControllerTest {

    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Test
    void checkEmailExistenceForUnknownEmail() throws Exception {
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-emails/{identifier}", emailAddress))
                .andExpect(status().isNotFound());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(emailAddress);
    }

    @Test
    void checkEmailExistenceForKnownEmail() throws Exception {
        Mockito.doReturn(Optional.of(new PortalUserIdentifierVerification()))
                .when(portalUserIdentifierRepository).findByIdentifier(Mockito.any());
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-emails/{identifier}", emailAddress))
                .andExpect(status().isOk());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findByIdentifier(emailAddress);
    }
}