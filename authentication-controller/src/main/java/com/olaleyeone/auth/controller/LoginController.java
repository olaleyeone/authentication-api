package com.olaleyeone.auth.controller;

import com.github.olaleyeone.auth.annotations.Public;
import com.github.olaleyeone.rest.ApiResponse;
import com.github.olaleyeone.rest.exception.ErrorResponse;
import com.olaleyeone.auth.data.entity.PortalUserAuthentication;
import com.olaleyeone.auth.data.enums.AuthenticationResponseType;
import com.olaleyeone.auth.data.dto.LoginApiRequest;
import com.olaleyeone.auth.response.handler.AccessTokenApiResponseHandler;
import com.olaleyeone.auth.response.pojo.AccessTokenApiResponse;
import com.olaleyeone.auth.service.LoginAuthenticationService;
import com.olaleyeone.data.dto.RequestMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Provider;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginAuthenticationService authenticationService;
    private final Provider<RequestMetadata> requestMetadata;
    private final AccessTokenApiResponseHandler accessTokenApiResponseHandler;

    @Public
    @PostMapping("/login")
    public HttpEntity<AccessTokenApiResponse> login(@Valid @RequestBody LoginApiRequest dto) {
        PortalUserAuthentication portalUserAuthentication = authenticationService.getAuthenticationResponse(dto, requestMetadata.get());
        if (portalUserAuthentication.getResponseType() != AuthenticationResponseType.SUCCESSFUL) {
            if (portalUserAuthentication.getResponseType() == AuthenticationResponseType.INACTIVE_ACCOUNT) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, new ApiResponse<>(null, "Inactive Account", null));
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, new ApiResponse<>(null, "Invalid Credentials", null));
        }

        return accessTokenApiResponseHandler.getAccessToken(portalUserAuthentication);
    }
}
