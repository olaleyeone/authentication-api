package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.olaleyeone.auth.data.dto.PasswordLoginApiRequest;
import com.olaleyeone.auth.data.dto.TotpLoginApiRequest;
import com.olaleyeone.auth.data.entity.authentication.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.integration.events.SessionUpdateEvent;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.PasswordLoginAuthenticationService;
import com.olaleyeone.auth.service.TotpLoginAuthenticationService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class TotpLoginController {

    private final TotpLoginAuthenticationService authenticationService;
    private final Provider<RequestMetadata> requestMetadata;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;
    private final ApplicationContext applicationContext;

    @Public
    @PostMapping("/totp/login")
    public HttpEntity<AccessTokenApiResponse> totpLogin(@Valid @RequestBody TotpLoginApiRequest dto) {
        PortalUserAuthentication portalUserAuthentication = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
        if (portalUserAuthentication.getResponseType() != AuthenticationResponseType.SUCCESSFUL) {
            if (portalUserAuthentication.getResponseType() == AuthenticationResponseType.INACTIVE_ACCOUNT) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, new ApiResponse<>(null, "Inactive Account", null));
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, new ApiResponse<>(null, "Invalid Credentials", null));
        }
        HttpEntity<AccessTokenApiResponse> accessToken = accessTokenApiResponseHandler.getAccessToken(portalUserAuthentication);
        applicationContext.publishEvent(new SessionUpdateEvent(portalUserAuthentication));
        return accessToken;
    }
}
