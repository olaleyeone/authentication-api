package com.olaleyeone.auth.controller;

import com.olaleyeone.auth.controllertest.ControllerTest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.email.PasswordResetTokenEmailSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PasswordResetRequestService;
import com.olaleyeone.data.dto.RequestMetadata;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Provider;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordResetRequestControllerTest extends ControllerTest {

    @Autowired
    private PasswordResetRequestService passwordResetRequestService;
    @Autowired
    private PortalUserIdentifierRepository portalUserIdentifierRepository;
    @Autowired
    private PasswordResetTokenEmailSender passwordResetTokenEmailSender;
    @Autowired
    private Provider<RequestMetadata> requestMetadataProvider;

    @Test
    void requestPasswordResetWithUnknownEmail() throws Exception {
        Mockito.doReturn(Optional.empty()).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any(), Mockito.any());
        String emailAddress = faker.internet().emailAddress();
        mockMvc.perform(MockMvcRequestBuilders.post("/password-resets")
                .param("email", emailAddress))
                .andExpect(status().isOk());
        Mockito.verify(portalUserIdentifierRepository, Mockito.times(1))
                .findActiveByIdentifier(emailAddress, UserIdentifierType.EMAIL_ADDRESS);
    }

    @Test
    void requestPasswordResetWithEmail() throws Exception {
        String emailAddress = faker.internet().emailAddress();
        PortalUserIdentifier portalUserIdentifier = new PortalUserIdentifier();
        Map.Entry<PasswordResetRequest, String> reset = Pair.of(new PasswordResetRequest(), faker.code().ean8());
        Mockito.doReturn(reset).when(passwordResetRequestService).createRequest(Mockito.any(), Mockito.anyBoolean());

        Mockito.doReturn(Optional.of(portalUserIdentifier)).when(portalUserIdentifierRepository).findActiveByIdentifier(Mockito.any(), Mockito.any());
        String domainName = faker.internet().domainName();
        mockMvc.perform(MockMvcRequestBuilders.post("/password-resets")
                .header("Host", domainName)
                .param("email", emailAddress))
                .andExpect(status().isOk());
        Mockito.verify(passwordResetRequestService, Mockito.times(1))
                .createRequest(Mockito.eq(portalUserIdentifier), Mockito.anyBoolean());
        Mockito.verify(passwordResetTokenEmailSender, Mockito.times(1))
                .sendResetLink(reset.getKey(), domainName);
    }
}