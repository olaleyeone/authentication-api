package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.email.PasswordResetTokenEmailSender;
import com.olaleyeone.auth.integration.sms.SmsSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PasswordResetRequestService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Provider;
import javax.validation.constraints.Email;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
public class PasswordResetRequestController {

    private final PasswordResetRequestService passwordResetRequestService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PasswordResetTokenEmailSender passwordResetTokenEmailSender;
    private final Provider<RequestMetadata> requestMetadataProvider;
    private final SmsSender smsSender;

    @Public
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/user-identifiers/{identifier}/reset-links")
    public ApiResponse<Void> requestPasswordResetLink(
            @PathVariable("identifier") @Email String identifier,
            @RequestParam(name = "autoLogin", required = false) boolean autoLogin) {
        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
                .orElse(null);
        if (portalUserIdentifier == null) {
            return new ApiResponse<>(null, "Successful", null);
        }
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier, autoLogin);
        PasswordResetRequest passwordResetRequest = request.getKey();
        passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, requestMetadataProvider.get().getHost());
        return new ApiResponse<>(null, "Successful", null);
    }

    @Public
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/user-identifiers/{identifier}/reset-codes")
    public ApiResponse<Void> requestPasswordResetCode(
            @PathVariable("identifier") String identifier,
            @RequestParam(name = "autoLogin", required = false) boolean autoLogin) {
        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
                .orElse(null);
        if (portalUserIdentifier == null) {
            return new ApiResponse<>(null, "Successful", null);
        }
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier, autoLogin);
        PasswordResetRequest passwordResetRequest = request.getKey();
        if (portalUserIdentifier.getIdentifierType() == UserIdentifierType.EMAIL) {
            passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, requestMetadataProvider.get().getHost());
        }
        if (portalUserIdentifier.getIdentifierType() == UserIdentifierType.PHONE_NUMBER) {
            smsSender.sendResetCode(passwordResetRequest);
        }
        return new ApiResponse<>(null, "Successful", null);
    }
}
