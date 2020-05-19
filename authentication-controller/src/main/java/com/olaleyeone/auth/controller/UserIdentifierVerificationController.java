package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.PortalUserIdentifierVerification;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.email.VerificationEmailSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.service.PortalUserIdentifierVerificationService;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Email;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
public class UserIdentifierVerificationController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PortalUserIdentifierVerificationService portalUserIdentifierVerificationService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final VerificationEmailSender verificationEmailSender;

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user-emails/{email}/verification-code")
    public void requestEmailVerificationCode(@PathVariable @Email String email) {
        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(email);
        if (optionalPortalUserIdentifier.isPresent() && BooleanUtils.isTrue(optionalPortalUserIdentifier.get().getVerified())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, ApiResponse.builder()
                    .message(String.format("Email %s already verified by user", email))
                    .build());
        }
        Map.Entry<PortalUserIdentifierVerification, String> verification
                = portalUserIdentifierVerificationService.createVerification(email, UserIdentifierType.EMAIL);
        try {
            verificationEmailSender.sendVerificationCode(verification.getKey(), verification.getValue());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

//    @Public
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping("/user-phone-numbers/{identifier}/verification-code")
//    public void requestPhoneNumberVerificationCode(@PathVariable @ValidPhoneNumber String identifier) {
//        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findByIdentifier(identifier);
//        if (optionalPortalUserIdentifier.isPresent() && BooleanUtils.isTrue(optionalPortalUserIdentifier.get().getVerified())) {
//            throw new ErrorResponse(HttpStatus.CONFLICT, ApiResponse.builder()
//                    .message(String.format("Phone number %s already verified by user"))
//                    .build());
//        }
//        portalUserIdentifierVerificationService.createVerification(identifier, UserIdentifierType.PHONE_NUMBER);
//    }
}
