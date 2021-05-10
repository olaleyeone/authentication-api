package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.github.olaleyeone.rest.exception.NotFoundException;
import com.olaleyeone.auth.data.dto.PasswordResetApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserIdentifier;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.data.enums.UserIdentifierType;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.repository.PasswordResetRequestRepository;
import com.olaleyeone.auth.repository.PortalUserIdentifierRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@RestController
public class PasswordResetController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PortalUserIdentifierRepository portalUserIdentifierRepository;
    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordUpdateService passwordUpdateService;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    private final ApplicationContext applicationContext;

    @JwtToken(JwtTokenType.PASSWORD_RESET)
    private final AccessClaimsExtractor accessClaimsExtractor;

    @Public
    @PostMapping(path = "/password", params = {"email", "resetToken"})
    public HttpEntity<AccessTokenApiResponse> resetPasswordWithEmailAndResetToken(
            @RequestParam("email") String email,
            @RequestParam("resetToken") String resetToken,
            @Valid @RequestBody PasswordResetApiRequest apiRequest) {

        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(email)
                .orElseThrow(NotFoundException::new);
        if (portalUserIdentifier.getIdentifierType() == UserIdentifierType.EMAIL) {
            AccessClaims claims;
            try {
                claims = accessClaimsExtractor.getClaims(resetToken);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            PasswordResetRequest passwordResetRequest = passwordResetRequestRepository.findById(Long.valueOf(claims.getId()))
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.FORBIDDEN, ApiResponse.builder()
                            .message("Unknown reset token")
                            .build()));
            return doReset(portalUserIdentifier, passwordResetRequest, apiRequest);
        }

        throw new ErrorResponse(HttpStatus.BAD_REQUEST);
    }

    @Public
    @PostMapping(path = "password-resets/{resetCode}/password")
    public HttpEntity<AccessTokenApiResponse> resetPasswordWithResetCode(
            @RequestParam("identifier") String identifier,
            @PathVariable("resetCode") String resetCode,
            @Valid @RequestBody PasswordResetApiRequest apiRequest) {

        PortalUserIdentifier portalUserIdentifier = portalUserIdentifierRepository.findActiveByIdentifier(identifier)
                .orElseThrow(NotFoundException::new);
        PasswordResetRequest passwordResetRequest = passwordResetRequestRepository.findByIdentifierAndCode(portalUserIdentifier, resetCode)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.FORBIDDEN, ApiResponse.builder()
                        .message("Unknown reset code")
                        .build()));
        return doReset(portalUserIdentifier, passwordResetRequest, apiRequest);
    }

    private HttpEntity<AccessTokenApiResponse> doReset(
            PortalUserIdentifier userIdentifier,
            PasswordResetRequest passwordResetRequest,
            PasswordResetApiRequest apiRequest) {
        if (passwordResetRequest.getUsedAt() != null) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, ApiResponse.builder()
                    .message("Reset code already used")
                    .build());
        }
        if (passwordResetRequest.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, ApiResponse.builder()
                    .message("Reset code has expired")
                    .build());
        }
        if (!passwordResetRequest.getPortalUserIdentifier().getId().equals(userIdentifier.getId())) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN, ApiResponse.builder()
                    .message("Invalid reset code")
                    .build());
        }
        HttpEntity<AccessTokenApiResponse> httpEntity = passwordUpdateService.updatePassword(passwordResetRequest, apiRequest)
                .map(portalUserAuthentication -> {
                    HttpEntity<AccessTokenApiResponse> accessToken = accessTokenApiResponseHandler.getAccessToken(portalUserAuthentication);
                    applicationContext.publishEvent(new SessionUpdateEvent(portalUserAuthentication));
                    return accessToken;
                })
                .orElseGet(() -> new HttpEntity<>(new AccessTokenApiResponse(passwordResetRequest.getPortalUser())));
        return httpEntity;
    }
}
