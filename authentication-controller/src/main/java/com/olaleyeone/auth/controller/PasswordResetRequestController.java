package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.email.PasswordResetTokenEmailSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PasswordResetRequestService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class PasswordResetRequestController {

    private final PasswordResetRequestService passwordResetRequestService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PasswordResetTokenEmailSender passwordResetTokenEmailSender;
    private final Provider<RequestMetadata> requestMetadataProvider;

    @Public
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/password-resets", params = "email")
    public void requestPasswordResetWithEmail(@RequestParam("email") String identifier) {
        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier, UserIdentifierType.EMAIL)
                .orElse(null);
        if (portalUserIdentifier == null) {
            return;
        }
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier);
        PasswordResetRequest passwordResetRequest = request.getKey();
        passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, requestMetadataProvider.get().getHost());
    }
}
