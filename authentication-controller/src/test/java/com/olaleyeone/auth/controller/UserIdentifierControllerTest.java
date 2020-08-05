package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIdentifierControllerTest extends ControllerTest {

    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Autowired
    private PhoneNumberService phoneNumberService;

    @Autowired
    private ValidPhoneNumber.Validator validPhoneNumberValidator;

    @Test
    void checkExistenceForUnknownEmail() throws Exception {
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-emails/{identifier}", emailAddress))
                .andExpect(status().isNotFound());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(emailAddress);
    }

    @Test
    void checkExistenceForKnownEmail() throws Exception {
        Mockito.doReturn(Optional.of(new PortalUserIdentifierVerification()))
                .when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-emails/{identifier}", emailAddress))
                .andExpect(status().isOk());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(emailAddress);
    }

    @Test
    void checkExistenceForUnknownPhoneNumber() throws Exception {
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        Mockito.doAnswer(invocation -> invocation.getArgument(0, String.class))
                .when(phoneNumberService).formatPhoneNumber(Mockito.anyString());

        String cellPhone = faker.phoneNumber().cellPhone();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-phone-numbers/{identifier}", cellPhone))
                .andExpect(status().isNotFound());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(cellPhone);
    }

    @Test
    void checkExistenceForKnownPhoneNumber() throws Exception {
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        Mockito.doAnswer(invocation -> invocation.getArgument(0, String.class))
                .when(phoneNumberService).formatPhoneNumber(Mockito.anyString());
        Mockito.doReturn(Optional.of(new PortalUserIdentifierVerification()))
                .when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        String cellPhone = faker.phoneNumber().cellPhone();
        mockMvc.perform(MockMvcRequestBuilders.head(
                "/user-phone-numbers/{identifier}", cellPhone))
                .andExpect(status().isOk());

        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(cellPhone);
    }
}