package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.dto.PortalUserIdentifierVerificationRequestMessage;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierVerificationRepository;
import com.olaleyeone.auth.service.PortalUserIdentifierVerificationService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
public class UserIdentifierVerificationController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PortalUserIdentifierVerificationService portalUserIdentifierVerificationService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final Provider<RequestMetadata> requestMetadataProvider;
    private final PortalUserIdentifierVerificationRepository portalUserIdentifierVerificationRepository;
    private final ApplicationContext applicationContext;

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user-emails/{email}/verification-code-requests")
    public void requestEmailVerificationCode(
            @PathVariable @Email String email,
            @RequestParam("name") Optional<String> optionalName,
            HttpServletRequest servletRequest) {
        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(email);
        if (optionalPortalUserIdentifier.isPresent() && BooleanUtils.isTrue(optionalPortalUserIdentifier.get().getVerified())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, ApiResponse.builder()
                    .message(String.format("Email %s already verified by user", email))
                    .build());
        }
        Map.Entry<PortalUserIdentifierVerification, String> verification
                = portalUserIdentifierVerificationService.createVerification(email, UserIdentifierType.EMAIL_ADDRESS);
        Map<String, Object> params = new HashMap<>();
        params.put("host", requestMetadataProvider.get().getHost());

        optionalName.ifPresent(name -> params.put("name", name));
        publishRequest(servletRequest, verification.getKey(), verification.getValue());
//        try {
//            verificationEmailSender.sendVerificationCode(verification.getKey(), verification.getValue(), params);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
    }

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user-phone-numbers/{identifier}/verification-code-requests")
    public void requestPhoneNumberVerificationCode(
            @PathVariable @ValidPhoneNumber String identifier,
            HttpServletRequest servletRequest) {
        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier);
        if (optionalPortalUserIdentifier.isPresent() && BooleanUtils.isTrue(optionalPortalUserIdentifier.get().getVerified())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, ApiResponse.builder()
                    .message(String.format("Phone number %s already verified by user", identifier))
                    .build());
        }
        Map.Entry<PortalUserIdentifierVerification, String> verification =
                portalUserIdentifierVerificationService.createVerification(identifier, UserIdentifierType.PHONE_NUMBER);
        publishRequest(servletRequest, verification.getKey(), verification.getValue());
    }

    @Public
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/user-identifiers/{identifier}/verification")
    public void verifyUserIdentifier(
            @PathVariable String identifier,
            @RequestParam("verificationCode") String verificationCode) {
        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
                .orElseThrow(NotFoundException::new);

        if (BooleanUtils.isTrue(portalUserIdentifier.getVerified())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, ApiResponse.builder()
                    .message(String.format("Identifier %s already verified by user", identifier))
                    .build());
        }
        PortalUserIdentifierVerification portalUserIdentifierVerification = portalUserIdentifierVerificationRepository.getAllActive(portalUserIdentifier.getIdentifier())
                .stream()
                .filter(userIdentifierVerification -> userIdentifierVerification.getIdentifier().equals(portalUserIdentifier.getIdentifier()))
                .findFirst()
                .orElse(null);
        if (portalUserIdentifierVerification == null) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, ApiResponse.builder()
                    .message(String.format("Invalid verification code", identifier))
                    .build());
        }

        portalUserIdentifierVerificationService.applyVerification(portalUserIdentifier, portalUserIdentifierVerification);
    }

    public void publishRequest(
            HttpServletRequest servletRequest,
            PortalUserIdentifierVerification identifierVerification,
            String code) {
        applicationContext.publishEvent(PortalUserIdentifierVerificationRequestMessage
                .builder()
                .identifier(identifierVerification.getIdentifier())
                .identifierType(identifierVerification.getIdentifierType())
                .requestHost(requestMetadataProvider.get().getHost())
                .requestQuery(servletRequest.getQueryString())
                .verificationCode(code)
                .createdAt(identifierVerification.getCreatedAt())
                .expiresAt(identifierVerification.getExpiresAt())
                .build());
    }
}
