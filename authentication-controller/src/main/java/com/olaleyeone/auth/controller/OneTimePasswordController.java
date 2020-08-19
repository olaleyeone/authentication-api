package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.olaleyeone.auth.constraints.ValidPhoneNumber;
import com.olaleyeone.auth.data.entity.OneTimePassword;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.integration.etc.PhoneNumberService;
import com.olaleyeone.auth.integration.sms.SmsSender;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.pojo.OtpApiResponse;
import com.olaleyeone.auth.service.OneTimePasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
public class OneTimePasswordController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OneTimePasswordService oneTimePasswordService;
    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PhoneNumberService phoneNumberService;
    private final SmsSender smsSender;

    @Public
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/user-phone-numbers/{phoneNumber}/otp")
    public ApiResponse<OtpApiResponse> requestOtp(@PathVariable @ValidPhoneNumber String phoneNumber) {
        String formattedPhoneNumber = phoneNumberService.formatPhoneNumber(phoneNumber);
        Optional<PortalUserIdentifier> optionalPortalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(formattedPhoneNumber);
        if (!optionalPortalUserIdentifier.isPresent()) {
            throw new ErrorResponse(HttpStatus.NOT_FOUND, ApiResponse.builder()
                    .message(String.format("No user found with phone number %s", phoneNumber))
                    .build());
        }

        PortalUserIdentifier identifier = optionalPortalUserIdentifier.get();
        Map.Entry<OneTimePassword, String> verification
                = oneTimePasswordService.createOTP(identifier);
        try {
            smsSender.sendOtp(verification.getKey(), verification.getValue());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ApiResponse<>(OtpApiResponse.builder()
                .identifier(identifier.getIdentifier())
                .transactionId(verification.getKey().getId().toString())
                .build(), "OTP created", null);
    }
}
