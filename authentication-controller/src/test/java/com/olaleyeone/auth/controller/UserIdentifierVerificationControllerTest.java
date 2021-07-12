package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.email.VerificationEmailSender;
import com.olaleyeone.auth.integration.sms.SmsSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PortalUserIdentifierVerificationService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserIdentifierVerificationControllerTest extends ControllerTest {

    @Autowired
    private VerificationEmailSender verificationEmailSender;

    @Autowired
    private PortalUserIdentifierVerificationService portalUserIdentifierVerificationService;

    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;

    @Autowired
    private ValidPhoneNumber.Validator validPhoneNumberValidator;

    @Autowired
    private SmsSender smsSender;

    private Pair<PortalUserIdentifierVerification, String> verificationResult;

    @BeforeEach
    public void setUp() {
        verificationResult = Pair.of(new PortalUserIdentifierVerification(), faker.code().ean8());
        Mockito.doReturn(verificationResult).when(portalUserIdentifierVerificationService)
                .createVerification(Mockito.any(), Mockito.any());
    }

    @Test
    void requestVerificationCode() throws Exception {
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.post("/user-emails/{identifier}/verification-code",
                emailAddress))
                .andExpect(status().isCreated());
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(emailAddress);
        Mockito.verify(portalUserIdentifierVerificationService, Mockito.times(1))
                .createVerification(emailAddress, UserIdentifierType.EMAIL_ADDRESS);
//        Mockito.verify(verificationEmailSender, Mockito.times(1))
//                .sendVerificationCode(verificationResult.getKey(), verificationResult.getValue());
    }

    @Test
    void requestVerificationCodeWithInvalidEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/user-emails/{identifier}/verification-code",
                faker.internet().ipV4Address()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestVerificationCodeWithVerifiedEmail() throws Exception {
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setVerified(true);
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/user-emails/{identifier}/verification-code",
                faker.internet().emailAddress()))
                .andExpect(status().isConflict());
    }

    @Test
    void requestVerificationCodeWithUnverifiedEmail() throws Exception {
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setVerified(false);
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/user-emails/{identifier}/verification-code",
                faker.internet().emailAddress()))
                .andExpect(status().isCreated());
    }

//    @Test
//    void requestVerificationCodeShouldNotPropagateEmailError() throws Exception {
//        Mockito.doThrow(new RuntimeException()).when(verificationEmailSender).sendVerificationCode(Mockito.any(), Mockito.anyString());
//        mockMvc.perform(MockMvcRequestBuilders.post("/user-emails/{identifier}/verification-code",
//                faker.internet().emailAddress()))
//                .andExpect(status().isCreated());
//        Mockito.verify(verificationEmailSender, Mockito.times(1))
//                .sendVerificationCode(verificationResult.getKey(), verificationResult.getValue());
//    }

    @Test
    void requestVerificationCodeWithVerifiedPhoneNumber() throws Exception {
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setVerified(true);
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/verification-code",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isConflict());
    }

    @Test
    void requestVerificationCodeWithUnverifiedPhoneNumber() throws Exception {
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        portalUserIdentifier.setVerified(false);
        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/verification-code",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isCreated());
    }

    @Test
    void requestVerificationCodeShouldNotPropagateSmsError() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(smsSender).sendVerificationCode(Mockito.any(), Mockito.anyString());
        Mockito.doReturn(true).when(validPhoneNumberValidator).isValid(Mockito.any(), Mockito.any());
        Mockito.doReturn(Optional.empty()).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/user-phone-numbers/{identifier}/verification-code",
                faker.phoneNumber().cellPhone()))
                .andExpect(status().isCreated());
        Mockito.verify(smsSender, Mockito.times(1))
                .sendVerificationCode(Mockito.any(), Mockito.anyString());
    }
}