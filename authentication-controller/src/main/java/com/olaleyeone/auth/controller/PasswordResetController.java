package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.auth.data.AccessClaims;
import com.github.olaleyeone.auth.data.AccessClaimsExtractor;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.olaleyeone.auth.data.dto.PasswordUpdateApiRequest;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.entity.passwordreset.PasswordResetRequest;
import com.olaleyeone.auth.data.enums.JwtTokenType;
import com.olaleyeone.auth.qualifier.JwtToken;
import com.olaleyeone.auth.repository.PasswordResetRequestRepository;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.PasswordUpdateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class PasswordResetController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PasswordResetRequestRepository passwordResetRequestRepository;
    private final PasswordUpdateService passwordUpdateService;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    @JwtToken(JwtTokenType.PASSWORD_RESET)
    private final AccessClaimsExtractor accessClaimsExtractor;

    @Public
    @PutMapping(path = "/password", params = {"identifier", "resetToken"})
    public HttpEntity<AccessTokenApiResponse> resetPasswordWithResetToken(
            @RequestParam("identifier") String identifier,
            @RequestParam("resetToken") String resetToken,
            @Valid @RequestBody PasswordUpdateApiRequest apiRequest) {
        AccessClaims claims;
        try {
            claims = accessClaimsExtractor.getClaims(resetToken);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        PasswordResetRequest passwordResetRequest = passwordResetRequestRepository.findById(Long.valueOf(claims.getId()))
                .orElseThrow(() -> new ErrorResponse(HttpStatus.FORBIDDEN));
        if (passwordResetRequest.getUsedOn() != null) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN);
        }
        if (passwordResetRequest.getExpiresOn().isBefore(LocalDateTime.now())) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN);
        }
        if (!passwordResetRequest.getPortalUserIdentifier().getIdentifier().equals(identifier)) {
            throw new ErrorResponse(HttpStatus.FORBIDDEN);
        }
        PortalUserAuthentication userAuthentication = passwordUpdateService.updatePassword(passwordResetRequest, apiRequest);
        return accessTokenApiResponseHandler.getAccessToken(userAuthentication);
    }
}
