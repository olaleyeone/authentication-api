package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.dto.PasswordResetRequestMessage;
import com.olaleyeone.auth.integration.security.PasswordResetTokenGenerator;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PasswordResetRequestService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
public class PasswordResetRequestController {

    private final PasswordResetRequestService passwordResetRequestService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final Provider<RequestMetadata> requestMetadataProvider;
    private final ApplicationContext applicationContext;
    private final PasswordResetTokenGenerator passwordResetTokenGenerator;

//    @Public
//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping(path = "/user-identifiers/{identifier}/reset-links")
//    public ApiResponse<Void> requestPasswordResetLink(
//            @PathVariable("identifier") @Email String identifier,
//            @RequestParam(name = "autoLogin", required = false) boolean autoLogin,
//            HttpServletRequest servletRequest) {
//        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
//                .orElse(null);
//        if (portalUserIdentifier == null) {
//            return new ApiResponse<>(null, "Successful", null);
//        }
//        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier, autoLogin);
//        PasswordResetRequest passwordResetRequest = request.getKey();
//        passwordResetTokenEmailSender.sendResetLink(passwordResetRequest, requestMetadataProvider.get().getHost());
//        return new ApiResponse<>(null, "Successful", null);
//    }

    @Public
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/user-identifiers/{identifier}/password-reset-requests")
    public ApiResponse<Void> requestPasswordResetCode(
            @PathVariable("identifier") String identifier,
            @RequestParam(name = "autoLogin", required = false) boolean autoLogin,
            HttpServletRequest servletRequest) {
        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
                .orElse(null);
        if (portalUserIdentifier == null) {
            return new ApiResponse<>(null, "Successful", null);
        }
        Map.Entry<PasswordResetRequest, String> request = passwordResetRequestService.createRequest(portalUserIdentifier, autoLogin);
        publishRequest(servletRequest, request.getKey(), request.getValue());
        return new ApiResponse<>(null, "Successful", null);
    }

    public void publishRequest(
            HttpServletRequest servletRequest,
            PasswordResetRequest passwordResetRequest,
            String code) {
        PortalUserIdentifier portalUserIdentifier = passwordResetRequest.getPortalUserIdentifier();
        applicationContext.publishEvent(PasswordResetRequestMessage
                .builder()
                .identifier(portalUserIdentifier.getIdentifier())
                .identifierType(portalUserIdentifier.getIdentifierType())
                .requestHost(requestMetadataProvider.get().getHost())
                .requestQuery(servletRequest.getQueryString())
                .resetCode(code)
                .resetToken(passwordResetTokenGenerator.generateJwt(passwordResetRequest).getToken())
                .createdAt(passwordResetRequest.getCreatedAt())
                .expiresAt(passwordResetRequest.getExpiresAt())
                .build());
    }
}
