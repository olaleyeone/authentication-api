package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.sms.SmsSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.OneTimePasswordService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OneTimePasswordControllerTest extends ControllerTest {

    @Autowired
    private OneTimePasswordService oneTimePasswordService;
    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;
    @Autowired
    private PhoneNumberService phoneNumberService;
    @Autowired
    private SmsSender smsSender;

    @Autowired
    private ValidPhoneNumber.Validator validPhoneNumberValidator;

    @Test
    void requestOtp() throws Exception {
        Mockito.doAnswer(invocation -> invocation.getArgument(0, String.class))
                .when(phoneNumberService).formatPhoneNumber(Mockito.any());
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());

        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setId(faker.number().randomNumber());
        String password = faker.internet().password();
        Mockito.doReturn(Pair.of(oneTimePassword, password))
                .when(oneTimePasswordService).createOTP(portalUserIdentifier);

        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/otp",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isCreated());
        Mockito.verify(oneTimePasswordService, Mockito.times(1))
                .createOTP(portalUserIdentifier);
        Mockito.verify(smsSender, Mockito.times(1))
                .sendOtp(oneTimePassword, password);
    }

    @Test
    void requestOtpWithSmsDeliveryError() throws Exception {
        Mockito.doAnswer(invocation -> invocation.getArgument(0, String.class))
                .when(phoneNumberService).formatPhoneNumber(Mockito.any());
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());

        OneTimePassword oneTimePassword = new OneTimePassword();
        oneTimePassword.setId(faker.number().randomNumber());
        String password = faker.internet().password();
        Mockito.doReturn(Pair.of(oneTimePassword, password))
                .when(oneTimePasswordService).createOTP(portalUserIdentifier);

        Mockito.doThrow(new RuntimeException()).when(smsSender).sendOtp(Mockito.any(), Mockito.any());

        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/otp",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isCreated());
        Mockito.verify(oneTimePasswordService, Mockito.times(1))
                .createOTP(portalUserIdentifier);
        Mockito.verify(smsSender, Mockito.times(1))
                .sendOtp(oneTimePassword, password);
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