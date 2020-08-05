package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.OneTimePasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OneTimePasswordControllerTest extends ControllerTest {

    @Autowired
    private OneTimePasswordService oneTimePasswordService;
    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;
    @Autowired
    private PhoneNumberService phoneNumberService;

    @Autowired
    private ValidPhoneNumber.Validator validPhoneNumberValidator;

    @Test
    void requestOtp() throws Exception {
        Mockito.doAnswer(invocation -> invocation.getArgument(0, String.class))
                .when(phoneNumberService).formatPhoneNumber(Mockito.any());
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/otp",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isCreated());
        Mockito.verify(oneTimePasswordService, Mockito.times(1))
                .createOTP(portalUserIdentifier);
    }

    @Test
    void requestOtpForInvalidPhoneNumber() throws Exception {
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        Mockito.doReturn(Optional.empty()).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/otp",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isNotFound());
        Mockito.verify(oneTimePasswordService, Mockito.never())
                .createOTP(Mockito.any());
    }
}